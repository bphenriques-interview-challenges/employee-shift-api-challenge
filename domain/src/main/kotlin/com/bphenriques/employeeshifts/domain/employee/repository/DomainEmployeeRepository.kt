package com.bphenriques.employeeshifts.domain.employee.repository

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeConstraintViolationException
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import kotlin.jvm.Throws

interface DomainEmployeeRepository {

    @Throws(EmployeeConstraintViolationException::class)
    suspend fun upsert(employee: Employee): Employee

    @Throws(EmployeeNotFoundException::class)
    suspend fun get(id: Int): Employee

    suspend fun delete(id: Int)
}
