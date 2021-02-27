package com.bphenriques.test

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.shift.model.Shift
import java.time.Instant
import java.util.Random
import java.util.UUID

object Generator {
    private val random = Random(10L) // Fixed seed for reproducibility!

    fun randomInt() = random.nextInt()
    fun uuid() = UUID.randomUUID().toString()

    fun stringOfLength(length: Int): String = (1..length).map { "a" }.joinToString("") { it }

    fun newEmployee() = Employee(
        id = 0,
        firstName = uuid(),
        lastName = uuid(),
        address = uuid()
    )

    fun newShift() = Shift(
        id = 0,
        employeeId = randomInt(),
        startShift = Instant.now(),
        endShift = Instant.now().plusSeconds(3600)
    )
}
