package com.bphenriques.employeeshifts.infrastructure.psql

import com.bphenriques.employeeshifts.domain.employee.model.ConflictingEmployeeException
import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.employee.repository.DomainEmployeeRepository
import io.r2dbc.spi.R2dbcDataIntegrityViolationException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

interface RawEmployeeRepository : CoroutineCrudRepository<EmployeeRow, Int>

@Repository
class EmployeeRepository(
    private val rawEmployeeRepository: RawEmployeeRepository
) : DomainEmployeeRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun create(employee: Employee): Employee {
        try {
            logger.debug("[CREATE] $employee")
            val savedEmployee = rawEmployeeRepository.save(employee.toEmployeeRow())
            logger.trace("[CREATED] $savedEmployee")
            return savedEmployee.toEmployee()
        } catch (ex: R2dbcDataIntegrityViolationException) {
            logger.debug("[ERROR] Constraint violation when creating employee ($employee): ${ex.message}", ex)
            throw ConflictingEmployeeException(employee)
        }
    }

    override suspend fun get(id: Int): Employee {
        logger.debug("[GET] $id")
        return rawEmployeeRepository.findById(id)?.toEmployee() ?: throw EmployeeNotFoundException(id)
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
