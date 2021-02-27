package com.bphenriques.employeeshifts.shift

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.shift.model.Shift
import com.bphenriques.employeeshifts.testhelper.EmployeeTestClient
import com.bphenriques.employeeshifts.testhelper.SQLUtil
import com.bphenriques.employeeshifts.testhelper.ShiftTestClient
import com.bphenriques.employeeshifts.testhelper.truncatedToMinute
import com.bphenriques.test.Generator.newEmployee
import com.bphenriques.test.Generator.newShift
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.annotation.DirtiesContext
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class SwapShiftsTests {

    @Autowired
    private lateinit var employeeTestClient: EmployeeTestClient

    @Autowired
    private lateinit var shiftTestClient: ShiftTestClient

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    private val now = Instant.parse("2020-01-01T12:00:00Z")

    @BeforeEach
    fun setup() {
        SQLUtil.clearAll(databaseClient)
    }

    @Test
    fun `Two employees can exchange shifts`() {
        val employee1 = createEmployee(newEmployee())
        val employee2 = createEmployee(newEmployee())
        val shiftEmployee1 = newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plus(1, ChronoUnit.HOURS))
        val shiftEmployee2 = newShift().copy(employeeId = employee2.id, startShift = now.plus(2, ChronoUnit.HOURS), endShift = now.plus(3, ChronoUnit.HOURS))

        // Create
        val createdShifts = shiftTestClient.upsertShifts(listOf(shiftEmployee1, shiftEmployee2)).run {
            expectStatus().isOk
            shiftTestClient.extractShifts(this)
        }
        Assertions.assertEquals(listOf(shiftEmployee1, shiftEmployee2).truncatedToMinute(), createdShifts.map { it.copy(id = 0) })
        val storedShiftEmployee1 = createdShifts.first { it.employeeId == employee1.id }
        val storedShiftEmployee2 = createdShifts.first { it.employeeId == employee2.id }

        // Swap
        val (swapedShift1, swappedShift2) = swapShifts(storedShiftEmployee1, storedShiftEmployee2)
        shiftTestClient.upsertShifts(listOf(swapedShift1, swappedShift2)).expectStatus().isOk

        // Check each employee
        val shiftsEmployee1 = shiftTestClient.findShiftsByEmployeeId(listOf(employee1.id))
        Assertions.assertEquals(listOf(swapedShift1), shiftTestClient.extractShifts(shiftsEmployee1))
        val shiftsEmployee2 = shiftTestClient.findShiftsByEmployeeId(listOf(employee2.id))
        Assertions.assertEquals(listOf(swappedShift2), shiftTestClient.extractShifts(shiftsEmployee2))
    }

    @Test
    fun `Can't exchange shifts if it leads to overlapping shifts`() {
        val employee1 = createEmployee(newEmployee())
        val employee2 = createEmployee(newEmployee())
        val shift1Employee1 = newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plus(1, ChronoUnit.HOURS))
        val shift2Employee1 = newShift().copy(employeeId = employee1.id, startShift = now.plus(1, ChronoUnit.HOURS), endShift = now.plus(2, ChronoUnit.HOURS))
        val shift1Employee2 = newShift().copy(employeeId = employee2.id, startShift = now.plus(1, ChronoUnit.MINUTES), endShift = now.plus(1, ChronoUnit.HOURS))
        val allShifts = listOf(shift1Employee1, shift2Employee1, shift1Employee2)

        // Create
        val createdShifts = shiftTestClient.upsertShifts(allShifts).run {
            expectStatus().isOk
            shiftTestClient.extractShifts(this)
        }
        Assertions.assertEquals(allShifts.truncatedToMinute(), createdShifts.map { it.copy(id = 0) })
        val storedShift1Employee1 = createdShifts.first { it.employeeId == employee1.id }
        val storedShift2Employee1 = createdShifts.filter { it.employeeId == employee1.id }[1]
        val storedShiftEmployee2 = createdShifts.first { it.employeeId == employee2.id }

        // Swap. The only shift of employee 2 overlaps with the already existing shift of employee 1
        val (swapedShift1, swappedShift2) = swapShifts(storedShift2Employee1, storedShiftEmployee2)
        shiftTestClient.upsertShifts(listOf(swapedShift1, swappedShift2)).expectStatus().isBadRequest

        // Check that the original shifts remain unchanged
        val shiftsEmployee1 = shiftTestClient.findShiftsByEmployeeId(listOf(employee1.id))
        Assertions.assertEquals(listOf(storedShift1Employee1, storedShift2Employee1), shiftTestClient.extractShifts(shiftsEmployee1))
        val shiftsEmployee2 = shiftTestClient.findShiftsByEmployeeId(listOf(employee2.id))
        Assertions.assertEquals(listOf(storedShiftEmployee2), shiftTestClient.extractShifts(shiftsEmployee2))
    }

    private fun swapShifts(left: Shift, right: Shift): Pair<Shift, Shift> =
        left.copy(startShift = right.startShift, endShift = right.endShift) to
            right.copy(startShift = left.startShift, endShift = left.endShift)

    private fun createEmployee(employee: Employee): Employee =
        employeeTestClient.createEmployee(employee).run {
            expectStatus().isCreated
            employeeTestClient.extractEmployee(this)
        }
}
