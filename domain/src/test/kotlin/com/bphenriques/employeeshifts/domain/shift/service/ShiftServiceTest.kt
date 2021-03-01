package com.bphenriques.employeeshifts.domain.shift.service

import com.bphenriques.employeeshifts.domain.shift.model.Shift
import com.bphenriques.employeeshifts.domain.shift.repository.DomainShiftRepository
import com.bphenriques.test.Generator.newShift
import com.bphenriques.test.Generator.randomInt
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.temporal.ChronoUnit

class ShiftServiceTest {

    private val repository = mockk<DomainShiftRepository>(relaxUnitFun = true)
    private val subject = ShiftService(
        repository = repository
    )

    @Test
    fun `upsert - It truncates the instant provided and return the repository result`() = runBlocking {
        val shifts = listOf(newShift(), newShift(), newShift())
        val normalizedShifts = shifts.map { shift ->
            shift.copy(
                startShift = shift.startShift.truncatedTo(ChronoUnit.MINUTES),
                endShift = shift.endShift.truncatedTo(ChronoUnit.MINUTES),
            )
        }
        val storedShifts = normalizedShifts.map { it.copy(id = randomInt()) }
        val passedShiftsRepository = slot<List<Shift>>()
        coEvery { repository.upsert(capture(passedShiftsRepository)) } returns storedShifts

        val result = subject.upsert(shifts)

        Assertions.assertEquals(normalizedShifts, passedShiftsRepository.captured.toList())
        Assertions.assertEquals(storedShifts, result.toList())
        coVerify { repository.upsert(any()) }
        confirmVerified(repository)
    }

    @Test
    fun `delete - It delegates the operation to the repository`() = runBlocking {
        val ids = listOf(randomInt(), randomInt(), randomInt())
        val passedShiftsRepository = slot<List<Int>>()
        coJustRun { repository.delete(capture(passedShiftsRepository)) }

        subject.delete(ids)

        Assertions.assertEquals(ids, passedShiftsRepository.captured.toList())
        coVerify { repository.delete(any()) }
        confirmVerified(repository)
    }

    @Test
    fun `findByEmployeeIds - It delegates the operation to the repository`() = runBlocking {
        val employeeIds = listOf(randomInt(), randomInt(), randomInt())
        val storedShifts = listOf(newShift(), newShift())
        val passedShiftsRepository = slot<List<Int>>()
        coEvery { repository.findByEmployeeIds(capture(passedShiftsRepository)) } returns storedShifts

        val result = subject.findByEmployeeIds(employeeIds)

        Assertions.assertEquals(employeeIds, passedShiftsRepository.captured.toList())
        Assertions.assertEquals(storedShifts.toList(), result.toList())
        coVerify { repository.findByEmployeeIds(any()) }
        confirmVerified(repository)
    }
}
