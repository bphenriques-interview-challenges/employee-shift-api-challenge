package com.bphenriques.employeeshifts.domain.shift.service

import com.bphenriques.employeeshifts.domain.shift.model.Shift
import com.bphenriques.employeeshifts.domain.shift.repository.DomainShiftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.temporal.ChronoUnit

@Service
class ShiftService(
    private val repository: DomainShiftRepository
) {

    suspend fun upsert(shifts: Flow<Shift>): Flow<Shift> {
        /**
         * Given the business context, shifts do not require precision greater than minute.
         *
         * Moreover this also avoids client-facing issues where client may send multiple shifts within the same minute
         * and having all of them accepted when they shouldn't be.
         *
         * Normalizing here, allows having a more flexible external contract.
         */
        val shiftsPrecisionToMinute = shifts.map {
            it.copy(
                startShift = it.startShift.truncatedTo(ChronoUnit.MINUTES),
                endShift = it.endShift.truncatedTo(ChronoUnit.MINUTES)
            )
        }
        return repository.upsert(shiftsPrecisionToMinute)
    }

    suspend fun delete(ids: Flow<Int>) {
        repository.delete(ids)
    }

    suspend fun findByEmployeeIds(employeeIds: Flow<Int>): Flow<Shift> {
        return repository.findByEmployeeIds(employeeIds)
    }
}
