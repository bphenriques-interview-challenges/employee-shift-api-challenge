package com.bphenriques.employeeshifts.domain.employee.service

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.employee.repository.DomainEmployeeRepository
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class EmployeeService(
    private val repository: DomainEmployeeRepository
) {

    suspend fun createEmployee(employee: Employee): Employee {
        return repository.create(employee)
    }

    @Throws(EmployeeNotFoundException::class)
    suspend fun getEmployee(id: Int): Employee {
        return repository.get(id)
    }
}
