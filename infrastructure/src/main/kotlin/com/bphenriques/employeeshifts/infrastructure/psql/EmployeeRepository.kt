package com.bphenriques.employeeshifts.infrastructure.psql

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeConstraintAlreadyInUseException
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeFieldsTooLargeException
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeUnmappedFailedOperation
import com.bphenriques.employeeshifts.domain.employee.repository.DomainEmployeeRepository
import org.slf4j.LoggerFactory
import org.springframework.data.relational.core.conversion.DbActionExecutionException
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface RawEmployeeRepository : CrudRepository<EmployeeRow, Int>

@Repository
class EmployeeRepository(
    private val rawEmployeeRepository: RawEmployeeRepository
) : DomainEmployeeRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun upsert(employee: Employee): Employee =
        try {
            val upsertedEmployee = rawEmployeeRepository.save(employee.toEmployeeRow())
            logger.info("[UPSERT EMPLOYEE] Upserted $upsertedEmployee")
            upsertedEmployee.toEmployee()
        } catch (ex: DbActionExecutionException) {
            logger.warn("[UPSERT EMPLOYEE] Error: Failed to perform action in the DB: ${ex.message}", ex)
            val message = ex.cause?.message
            when {
                // Ideally DbActionExecutionException provide directly the constraint being violated.
                message == null -> error("[EMPLOYEE] Data Integrity violation but lack of message which is unexpected.")
                message.contains("employee_address_key") -> throw EmployeeConstraintAlreadyInUseException(employee, ex)
                message.contains("value too long") -> throw EmployeeFieldsTooLargeException(employee, ex)
                else -> throw EmployeeUnmappedFailedOperation(employee, ex)
            }
        }

    override suspend fun get(id: Int): Employee {
        logger.debug("[GET EMPLOYEE] $id")
        return rawEmployeeRepository.findById(id).orElse(null)?.toEmployee() ?: throw EmployeeNotFoundException(id)
    }

    override suspend fun delete(id: Int) {
        rawEmployeeRepository.deleteById(id)
        logger.info("[DELETED EMPLOYEE] $id")
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
