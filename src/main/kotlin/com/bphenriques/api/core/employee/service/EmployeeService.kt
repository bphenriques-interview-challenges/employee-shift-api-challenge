package com.bphenriques.api.core.employee.service

import com.bphenriques.api.core.employee.infrastructure.psql.EmployeeRepository
import com.bphenriques.api.core.employee.infrastructure.psql.EmployeeRow
import com.bphenriques.api.core.employee.model.Employee
import org.springframework.stereotype.Service

@Service
class EmployeeService(
    private val repository: EmployeeRepository
) {

    suspend fun saveEmployee(employee: Employee): Employee {
        repository.save(employee.toEmployeeRow())
        return employee
    }

    suspend fun getEmployee(id: Long): Employee? {
        return repository.findById(id)?.toEmployee()
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
