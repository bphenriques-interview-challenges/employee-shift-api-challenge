package com.bphenriques.employeeshifts.domain.employee.model

data class EmployeeNotFoundException(val employeeId: Int) : RuntimeException(
    "Can't find requested employee $employeeId"
)

data class EmployeeConstraintViolationException(val employee: Employee, override val cause: Throwable) : RuntimeException(
    "Constraint violation for employee $employee: $cause", cause
)
