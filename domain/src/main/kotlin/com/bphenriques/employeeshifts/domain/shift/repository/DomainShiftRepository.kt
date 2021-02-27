package com.bphenriques.employeeshifts.domain.shift.repository

import com.bphenriques.employeeshifts.domain.shift.model.Shift
import com.bphenriques.employeeshifts.domain.shift.model.ShiftConstraintViolationException
import kotlinx.coroutines.flow.Flow

interface DomainShiftRepository {

    @Throws(ShiftConstraintViolationException::class)
    suspend fun upsert(shifts: Flow<Shift>): Flow<Shift>

    suspend fun findByEmployeeIds(employeeIds: Flow<Int>): Flow<Shift>

    suspend fun delete(ids: Flow<Int>)
}
