package com.bphenriques.employeeshifts.infrastructure.psql

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("employee")
data class EmployeeRow(
    @Id
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val address: String,
)
