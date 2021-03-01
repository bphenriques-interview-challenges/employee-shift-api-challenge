package com.bphenriques.employeeshifts.domain.employee.model

sealed class EmployeeRowOperationException(override val message: String, override val cause: Throwable? = null) : RuntimeException(
    message, cause
)

class EmployeeUnmappedFailedOperation(val employee: Employee, override val cause: Throwable) : EmployeeRowOperationException(
    "Unmapped failed operation on shift table (employee=$employee): ${cause.message}", cause
)

data class EmployeeNotFoundException(val employeeId: Int) : EmployeeRowOperationException(
    "Can't find requested employee $employeeId"
)

data class EmployeeConstraintAlreadyInUseException(val employee: Employee, override val cause: Throwable) : EmployeeRowOperationException(
    "There is already an employee using the provided address (address=${employee.address}): $cause", cause
)

data class EmployeeFieldsTooLargeException(val employee: Employee, override val cause: Throwable) : EmployeeRowOperationException(
    "The provided employee details exceeds the maximum number of characters (employee=$employee): $cause", cause
)
