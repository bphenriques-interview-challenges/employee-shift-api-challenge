package com.bphenriques.employeeshifts.infrastructure.psql

import com.bphenriques.employeeshifts.domain.shift.model.Shift
import com.bphenriques.employeeshifts.domain.shift.model.ShiftConstraintViolationException
import com.bphenriques.employeeshifts.domain.shift.repository.DomainShiftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface RawShiftRepository : CoroutineCrudRepository<ShiftRow, Int> {

    @Query("SELECT * FROM shift s WHERE s.employee_id IN (:ids)")
    fun findAllByEmployeeIds(@Param("ids") employeeIds: List<Int>): Flow<ShiftRow>
}

@Repository
class ShiftRepository(
    private val rawShiftRepository: RawShiftRepository,
) : DomainShiftRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * All or nothing operation. Otherwise, it will return the entries that were upserted successfully.
     */
    @Transactional
    override suspend fun upsert(shifts: Flow<Shift>): Flow<Shift> = rawShiftRepository
        .saveAll(shifts.map { it.toShiftRow() })
        .map {
            val result = it.toShift()
            logger.info("[UPSERT] Upserted $result")
            result
        }
        .catch { ex ->
            when (ex) {
                is DataIntegrityViolationException -> {
                    logger.warn("[UPSERT] Error: Constraint violation when upserting shifts: ${ex.message}", ex)
                    throw ShiftConstraintViolationException(shifts.toList(), ex)
                }
                else -> {
                    logger.error("Unexpected error encountered: $ex", ex)
                    throw ex
                }
            }
        }

    suspend fun get(ids: Flow<Int>): Flow<Shift> =
        rawShiftRepository.findAllById(ids).map { it.toShift() }

    override suspend fun findByEmployeeIds(employeeIds: Flow<Int>): Flow<Shift> {
        // @Query binding methods do not support Flow<*> and IN does not work with empty list.
        val employeeIdsList = employeeIds.toList()
        return if (employeeIdsList.isEmpty()) { // count() materializes the whole flow, I just need to check if it is empty.
            logger.trace("[FIND] Skipping due to empty collection provided")
            emptyFlow()
        } else {
            logger.debug("[FIND] $employeeIdsList")
            rawShiftRepository.findAllByEmployeeIds(employeeIdsList).map { it.toShift() }
        }
    }

    @Transactional
    override suspend fun delete(ids: Flow<Int>) {
        for (id in ids.toList()) {
            rawShiftRepository.deleteById(id)
            logger.info("[DELETE] Deleted $id")
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
