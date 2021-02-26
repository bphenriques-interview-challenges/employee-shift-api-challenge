package com.bphenriques.employeeshifts.domain.employee.service

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.infrastructure.psql.EmployeeRepository
import com.bphenriques.employeeshifts.infrastructure.psql.EmployeeRow
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class EmployeeService(
    private val repository: EmployeeRepository
) {

    suspend fun createEmployee(employee: Employee): Employee {
        val savedEmployee = repository.save(employee.toEmployeeRow())
        return savedEmployee.toEmployee()
    }

    @Throws(EmployeeNotFoundException::class)
    suspend fun getEmployee(id: Int): Employee {
        return repository.findById(id)?.toEmployee() ?: throw EmployeeNotFoundException(id)
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
