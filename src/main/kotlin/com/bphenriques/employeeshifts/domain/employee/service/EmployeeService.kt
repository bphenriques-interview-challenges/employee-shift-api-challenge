package com.bphenriques.employeeshifts.domain.employee.service

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.infrastructure.psql.EmployeeRepository
import com.bphenriques.employeeshifts.infrastructure.psql.EmployeeRow
import org.springframework.stereotype.Service

@Service
class EmployeeService(
    private val repository: EmployeeRepository
) {

    suspend fun saveEmployee(employee: Employee): Employee {
        repository.save(employee.toEmployeeRow())
        return employee
    }

    suspend fun getEmployee(id: Long): Employee {
        return repository.findById(id)?.toEmployee() ?: throw EmployeeNotFoundException(id)
    }

    private fun Employee.toEmployeeRow() = EmployeeRow(
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address
    )

    private fun EmployeeRow.toEmployee() = Employee(
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address
    )
}
