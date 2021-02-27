package com.bphenriques.employeeshifts.domain.shift.model

import java.time.Instant

data class Shift(
    val id: Int,
    val employeeId: Int,
    val startShift: Instant,
    val endShift: Instant
)
