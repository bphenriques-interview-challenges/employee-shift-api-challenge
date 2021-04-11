package com.bphenriques.employeeshifts.infrastructure.psql

import com.bphenriques.employeeshifts.domain.shift.model.Shift
import com.bphenriques.employeeshifts.domain.shift.model.ShiftConstraintEmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.shift.model.ShiftConstraintEndBeforeOrAtStartException
import com.bphenriques.employeeshifts.domain.shift.model.ShiftConstraintOverlappingShiftsException
import com.bphenriques.employeeshifts.domain.shift.model.ShiftUnmappedFailedOperation
import com.bphenriques.employeeshifts.domain.shift.repository.DomainShiftRepository
import org.slf4j.LoggerFactory
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.conversion.DbActionExecutionException
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface RawShiftRepository : CrudRepository<ShiftRow, Int> {

    @Query("SELECT * FROM shift s WHERE s.employee_id IN (:ids)")
    fun findAllByEmployeeIds(@Param("ids") employeeIds: List<Int>): List<ShiftRow>

    @Modifying
    @Query("DELETE FROM shift s WHERE s.id IN (:ids)")
    fun deleteAllById(@Param("ids") ids: List<Int>)
}

@Repository
class ShiftRepository(
    private val rawShiftRepository: RawShiftRepository,
) : DomainShiftRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Future work: Perform this operations in configurable batches.
     */
    @Transactional
    override suspend fun upsert(shifts: List<Shift>): List<Shift> =
        if (shifts.isEmpty()) {
            emptyList()
        } else {
            try {
                rawShiftRepository
                    .saveAll(shifts.map { it.toShiftRow() })
                    .map {
                        val result = it.toShift()
                        logger.info("[UPSERT SHIFT] Upserted $result")
                        result
                    }
            } catch (ex: DbActionExecutionException) {
                logger.warn("[UPSERT SHIFT] Error: Failed to perform action in the DB: ${ex.message}", ex)
                val message = ex.cause?.message
                when {
                    // Ideally DbActionExecutionException provide directly the constraint being violated.
                    message == null -> error("[SHIFT] Data Integrity violation but lack of message which is unexpected.")
                    message.contains("start_shift_after_end_shift") -> throw ShiftConstraintEndBeforeOrAtStartException(shifts.toList(), ex)
                    message.contains("employee_non_overlapping_shifts") -> throw ShiftConstraintOverlappingShiftsException(shifts.toList(), ex)
                    message.contains("fk_employee") -> throw ShiftConstraintEmployeeNotFoundException(shifts.toList(), ex)
                    else -> throw ShiftUnmappedFailedOperation(shifts.toList(), ex)
                }
            }
        }

    suspend fun get(ids: List<Int>): List<Shift> =
        if (ids.isEmpty()) {
            emptyList()
        } else {
            rawShiftRepository.findAllById(ids).map { it.toShift() }
        }

    override suspend fun findByEmployeeIds(employeeIds: List<Int>): List<Shift> =
        if (employeeIds.isEmpty()) {
            emptyList()
        } else {
            logger.debug("[FIND SHIFT] $employeeIds")
            rawShiftRepository.findAllByEmployeeIds(employeeIds).map { it.toShift() }
        }

    override suspend fun delete(ids: List<Int>) {
        if (ids.isNotEmpty()) {
            val idsList = ids.toList()
            rawShiftRepository.deleteAllById(idsList)
            logger.info("[DELETE SHIFT] Deleted $idsList")
        }
    }

    private fun Shift.toShiftRow() = ShiftRow(
        id = this.id,
        employeeId = this.employeeId,
        startShift = this.startShift,
        endShift = this.endShift,
    )

    private fun ShiftRow.toShift() = Shift(
        id = this.id,
        employeeId = this.employeeId,
        startShift = this.startShift,
        endShift = this.endShift,
    )
}
