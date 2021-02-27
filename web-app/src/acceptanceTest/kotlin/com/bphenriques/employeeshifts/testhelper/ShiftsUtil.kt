package com.bphenriques.employeeshifts.testhelper

import com.bphenriques.employeeshifts.domain.shift.model.Shift
import java.time.temporal.ChronoUnit

/**
 * Server truncates the received requests to minute.
 */
fun Shift.truncatedToMinute() = copy(
    startShift = startShift.truncatedTo(ChronoUnit.MINUTES),
    endShift = endShift.truncatedTo(ChronoUnit.MINUTES)
)
fun List<Shift>.truncatedToMinute() = this.map { it.truncatedToMinute() }
