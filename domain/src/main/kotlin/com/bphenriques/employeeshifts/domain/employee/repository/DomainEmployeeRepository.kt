package com.bphenriques.employeeshifts.domain.employee.repository

import com.bphenriques.employeeshifts.domain.employee.model.ConflictingEmployeeException
import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import kotlin.jvm.Throws

interface DomainEmployeeRepository {

    @Throws(ConflictingEmployeeException::class)
    suspend fun create(employee: Employee): Employee

    @Throws(EmployeeNotFoundException::class)
    suspend fun get(id: Int): Employee
}
