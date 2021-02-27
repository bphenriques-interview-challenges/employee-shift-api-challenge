// ktlint-disable filename
package com.bphenriques.employeeshifts.domain.shift.model

data class ShiftConstraintViolationException(val shifts: List<Shift>, override val cause: Throwable) : RuntimeException(
    "Conflicting shift upserts (shifts=$shifts): ${cause.message}", cause
)
