package com.bphenriques.employeeshifts.infrastructure.psql

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeConstraintViolationException
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.employee.repository.DomainEmployeeRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.r2dbc.BadSqlGrammarException
import org.springframework.stereotype.Repository

interface RawEmployeeRepository : CoroutineCrudRepository<EmployeeRow, Int>

@Repository
class EmployeeRepository(
    private val rawEmployeeRepository: RawEmployeeRepository
) : DomainEmployeeRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun upsert(employee: Employee): Employee {
        try {
            val upsertedEmployee = rawEmployeeRepository.save(employee.toEmployeeRow())
            logger.info("[UPSERT] Upserted $upsertedEmployee") // Remove PII.
            return upsertedEmployee.toEmployee()
        } catch (ex: DataIntegrityViolationException) {
            logger.warn("[ERROR] Constraint violation when upserting employee ($employee): ${ex.message}", ex)
            throw EmployeeConstraintViolationException(employee, ex)
        } catch (ex: BadSqlGrammarException) {
            logger.warn("[ERROR] Field too large when upserting employee: ${ex.message}", ex)
            throw EmployeeConstraintViolationException(employee, ex)
        }
    }

    override suspend fun get(id: Int): Employee {
        logger.debug("[GET] $id")
        return rawEmployeeRepository.findById(id)?.toEmployee() ?: throw EmployeeNotFoundException(id)
    }

    override suspend fun delete(id: Int) {
        logger.debug("[DELETE] $id")
        rawEmployeeRepository.deleteById(id)
        logger.info("[DELETED] $id")
    }

    private fun Employee.toEmployeeRow() = EmployeeRow(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address
    )

    private fun EmployeeRow.toEmployee() = Employee(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address
    )
}
