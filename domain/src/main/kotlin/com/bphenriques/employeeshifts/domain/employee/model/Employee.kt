package com.bphenriques.employeeshifts.domain.employee.model

data class Employee(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val address: String
) {
    companion object {
        const val MAX_LENGTH_FIRST_NAME = 50
        const val MAX_LENGTH_LAST_NAME = 50
        const val MAX_LENGTH_ADDRESS = 255
    }
}
