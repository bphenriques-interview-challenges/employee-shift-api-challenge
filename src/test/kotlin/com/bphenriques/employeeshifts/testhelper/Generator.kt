package com.bphenriques.employeeshifts.testhelper

import java.util.Random
import java.util.UUID

object Generator {
    private val random = Random(10L) // Fixed seed for reproducibility

    fun randomLong() = random.nextLong()
    fun uuid() = UUID.randomUUID().toString()
}
