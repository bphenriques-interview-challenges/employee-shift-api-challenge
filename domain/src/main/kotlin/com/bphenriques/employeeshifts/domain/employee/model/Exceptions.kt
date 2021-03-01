package com.bphenriques.employeeshifts.domain.employee.model

data class EmployeeNotFoundException(val employeeId: Int) : RuntimeException(
    "Can't find requested employee $employeeId"
)

data class EmployeeConstraintAlreadyInUseException(val employee: Employee, override val cause: Throwable) : RuntimeException(
    "There is already an employee using the provided address (address=${employee.address}): $cause", cause
)

data class EmployeeFieldsTooLargeException(val employee: Employee, override val cause: Throwable) : RuntimeException(
    "The provided employee details exceeds the maximum number of characters (employee=$employee): $cause", cause
)
