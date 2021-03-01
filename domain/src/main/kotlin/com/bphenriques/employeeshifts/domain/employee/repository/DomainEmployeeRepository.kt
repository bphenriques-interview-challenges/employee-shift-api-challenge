package com.bphenriques.employeeshifts.domain.employee.repository

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeConstraintAlreadyInUseException
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import kotlin.jvm.Throws

interface DomainEmployeeRepository {

    @Throws(EmployeeConstraintAlreadyInUseException::class)
    suspend fun upsert(employee: Employee): Employee

    @Throws(EmployeeNotFoundException::class)
    suspend fun get(id: Int): Employee

    suspend fun delete(id: Int)
}
