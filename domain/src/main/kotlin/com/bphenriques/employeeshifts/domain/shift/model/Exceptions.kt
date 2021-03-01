package com.bphenriques.employeeshifts.domain.shift.model

sealed class ShiftRowOperationException(
    override val message: String?,
    override val cause: Throwable?
) : RuntimeException(message, cause)

class ShiftUnmappedFailedOperation(val shifts: List<Shift>, override val cause: Throwable) : ShiftRowOperationException(
    "Unmapped failed operation on shift table (shifts=$shifts): ${cause.message}", cause
)

data class ShiftConstraintEmployeeNotFoundException(val shifts: List<Shift>, override val cause: Throwable) : ShiftRowOperationException(
    "One of the provided shifts uses an nonexistent employee (shifts=$shifts): ${cause.message}", cause
)

data class ShiftConstraintEndBeforeOrAtStartException(val shifts: List<Shift>, override val cause: Throwable) : ShiftRowOperationException(
    "One of the provided shifts has its shift ending at or before its start (shifts=$shifts): ${cause.message}", cause
)

data class ShiftConstraintOverlappingShiftsException(val shifts: List<Shift>, override val cause: Throwable) : ShiftRowOperationException(
    "One of the provided shifts would lead to overlapping shifts in an employee (shifts=$shifts): ${cause.message}", cause
)
