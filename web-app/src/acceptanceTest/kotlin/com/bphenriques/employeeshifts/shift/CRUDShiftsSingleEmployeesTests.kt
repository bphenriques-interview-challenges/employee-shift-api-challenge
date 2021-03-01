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
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CRUDShiftsSingleEmployeesTests {

    @Autowired
    private lateinit var employeeTestClient: EmployeeTestClient

    @Autowired
    private lateinit var shiftTestClient: ShiftTestClient

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    private val now = Instant.parse("2020-01-01T12:00:00Z")

    @BeforeEach
    fun setup() {
        SQLUtil.clearAll(jdbcTemplate)
    }

    @Test
    fun `Create, read, update, and delete shifts for single employee`() {
        val employee = createEmployee(newEmployee())
        val shift = newShift().copy(employeeId = employee.id, startShift = now, endShift = now.plus(1, ChronoUnit.MINUTES))

        // Create
        val createdShift = shiftTestClient.upsertShifts(listOf(shift)).run {
            expectStatus().isOk
            expectHeader().contentType(MediaType.APPLICATION_JSON)
            shiftTestClient.extractShifts(this)
        }.first()
        Assertions.assertEquals(shift.truncatedToMinute(), createdShift.copy(id = 0))

        // Find
        val fetchedShift = shiftTestClient.findShiftsByEmployeeId(listOf(employee.id)).run {
            expectStatus().isOk
            expectHeader().contentType(MediaType.APPLICATION_JSON)
            shiftTestClient.extractShifts(this)
        }.first()
        Assertions.assertEquals(createdShift, fetchedShift)

        // Update
        val updatedShift = fetchedShift.copy(
            startShift = fetchedShift.startShift.plus(1, ChronoUnit.HOURS),
            endShift = fetchedShift.endShift.plus(1, ChronoUnit.HOURS)
        )
        shiftTestClient.upsertShifts(listOf(updatedShift)).run {
            expectStatus().isOk
            expectHeader().contentType(MediaType.APPLICATION_JSON)
        }
        Assertions.assertEquals(
            updatedShift,
            shiftTestClient.findShiftsByEmployeeId(listOf(employee.id)).run {
                shiftTestClient.extractShifts(this)
            }.first()
        )

        // Delete
        shiftTestClient.deleteShiftsId(listOf(updatedShift.id)).run {
            expectStatus().isOk
        }
        Assertions.assertEquals(
            emptyList<Shift>(),
            shiftTestClient.findShiftsByEmployeeId(listOf(employee.id)).run { shiftTestClient.extractShifts(this) }
        )
    }

    @Test
    fun `It handles graciously duplicate update`() {
        val employee = createEmployee(newEmployee())
        val shift = newShift().copy(employeeId = employee.id, startShift = now, endShift = now.plus(1, ChronoUnit.MINUTES))

        // Create
        val createdShift = shiftTestClient.upsertShifts(listOf(shift)).run {
            expectStatus().isOk
            val createdShifts = shiftTestClient.extractShifts(this)
            Assertions.assertEquals(1, createdShifts.size)
            createdShifts
        }.first()

        // Submit many times the same update
        val updatedShift = createdShift.copy(startShift = createdShift.startShift.minus(1, ChronoUnit.HOURS))
        val duplicateUpdatedOperation = listOf(updatedShift, updatedShift)
        val updatedShifts = shiftTestClient.upsertShifts(duplicateUpdatedOperation).run {
            expectStatus().isOk
            shiftTestClient.extractShifts(this)
        }
        Assertions.assertEquals(duplicateUpdatedOperation, updatedShifts) // All updates were successful. We can apply an unique for :nail-polish:.
    }

    @Test
    fun `Empty upsert request returns nothing`() {
        val result = shiftTestClient.upsertShifts(emptyList()).run {
            expectStatus().isOk
            expectHeader().contentType(MediaType.APPLICATION_JSON)
            shiftTestClient.extractShifts(this)
        }

        Assertions.assertEquals(emptyList<Shift>(), result)
    }

    @Test
    fun `Providing empty list of ids when deleting returns bad request`() {
        shiftTestClient.deleteShiftsId(emptyList()).expectStatus().isBadRequest
    }

    private fun createEmployee(employee: Employee): Employee =
        employeeTestClient.createEmployee(employee).run {
            expectStatus().isCreated
            employeeTestClient.extractEmployee(this)
        }
}
