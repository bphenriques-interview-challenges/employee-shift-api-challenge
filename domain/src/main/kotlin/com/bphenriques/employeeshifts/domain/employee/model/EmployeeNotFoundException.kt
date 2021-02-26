package com.bphenriques.employeeshifts.domain.employee.model

data class EmployeeNotFoundException(val employeeId: Int) : RuntimeException(
    "Can't find request employee $employeeId"
)
