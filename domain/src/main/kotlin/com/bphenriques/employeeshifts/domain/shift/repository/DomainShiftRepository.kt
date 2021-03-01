package com.bphenriques.employeeshifts.domain.shift.repository

import com.bphenriques.employeeshifts.domain.shift.model.Shift
import com.bphenriques.employeeshifts.domain.shift.model.ShiftRowOperationException

interface DomainShiftRepository {

    @Throws(ShiftRowOperationException::class)
    suspend fun upsert(shifts: List<Shift>): List<Shift>

    suspend fun findByEmployeeIds(employeeIds: List<Int>): List<Shift>

    suspend fun delete(ids: List<Int>)
}
