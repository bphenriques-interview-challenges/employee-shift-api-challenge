package com.bphenriques.employeeshifts.domain.employee.service

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.repository.DomainEmployeeRepository
import org.springframework.stereotype.Service

@Service
class EmployeeService(
    private val repository: DomainEmployeeRepository
) {

    suspend fun upsert(employee: Employee): Employee {
        return repository.upsert(employee)
    }

    suspend fun get(id: Int): Employee {
        return repository.get(id)
    }

    suspend fun delete(id: Int) {
        repository.delete(id)
    }
}
