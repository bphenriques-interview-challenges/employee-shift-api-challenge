package com.bphenriques.employeeshifts.infrastructure.psql

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("shift")
data class ShiftRow(
    @Id
    val id: Int = 0,
    val employeeId: Int,
    val startShift: Instant,
    val endShift: Instant,
)
