package com.bphenriques.employeeshifts.shift

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.testhelper.EmployeeTestClient
import com.bphenriques.employeeshifts.testhelper.SQLUtil
import com.bphenriques.employeeshifts.testhelper.ShiftTestClient
import com.bphenriques.test.Generator
import com.bphenriques.test.Generator.newEmployee
import com.bphenriques.test.Generator.newShift
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.annotation.DirtiesContext
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ShiftsInvariantsTests {

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
    fun `The start of the shift must be after end shift`() {
        val employee = createEmployee(newEmployee())
        val invalidShifts = listOf(
            newShift().copy(employeeId = employee.id, startShift = now, endShift = now),
            newShift().copy(employeeId = employee.id, startShift = now, endShift = now.minus(1, ChronoUnit.HOURS))
        )

        shiftTestClient.upsertShifts(invalidShifts).expectStatus().isBadRequest
    }

    @Test
    fun `An employee can work on consecutive shifts`() {
        val employee = createEmployee(newEmployee())
        val invalidShifts = listOf(
            newShift().copy(employeeId = employee.id, startShift = now, endShift = now.plus(30, ChronoUnit.MINUTES)),
            newShift().copy(employeeId = employee.id, startShift = now.plus(30, ChronoUnit.MINUTES), endShift = now.plus(60, ChronoUnit.MINUTES))
        )

        shiftTestClient.upsertShifts(invalidShifts).expectStatus().isOk
    }

    @Test
    fun `An employee cannot work more than 1 shift at a time`() {
        val employee = createEmployee(newEmployee())
        val validShift = newShift().copy(employeeId = employee.id, startShift = now, endShift = now.plus(30, ChronoUnit.MINUTES))
        val invalidShifts = listOf(
            newShift().copy(employeeId = employee.id, startShift = now.minus(1, ChronoUnit.MINUTES), endShift = now.plus(1, ChronoUnit.MINUTES)),
            newShift().copy(employeeId = employee.id, startShift = now, endShift = now.plus(1, ChronoUnit.MINUTES))
        )
        val createValidShift = shiftTestClient.upsertShifts(listOf(validShift))
        createValidShift.expectStatus().isOk

        // Is rejected regardless if inserted in batch or not
        for (invalidShift in invalidShifts) {
            shiftTestClient.upsertShifts(listOf(invalidShift)).expectStatus().isEqualTo(HttpStatus.CONFLICT)
        }
        shiftTestClient.upsertShifts(invalidShifts).expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun `Multiple employees can have overlapping shifts`() {
        val employee1 = createEmployee(newEmployee())
        val employee2 = createEmployee(newEmployee())
        val employee3 = createEmployee(newEmployee())
        val employee4 = createEmployee(newEmployee())
        val validShifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plus(30, ChronoUnit.MINUTES)),
            newShift().copy(employeeId = employee2.id, startShift = now.minus(1, ChronoUnit.MINUTES), endShift = now.plus(1, ChronoUnit.MINUTES)), // left
            newShift().copy(employeeId = employee3.id, startShift = now.plus(1, ChronoUnit.MINUTES), endShift = now.plus(2, ChronoUnit.MINUTES)), // middle
            newShift().copy(employeeId = employee4.id, startShift = now.plus(29, ChronoUnit.MINUTES), endShift = now.plus(31, ChronoUnit.MINUTES)), // right
        )

        shiftTestClient.upsertShifts(validShifts).expectStatus().isOk
    }

    @Test
    fun `It returns NOT_FOUND (404) when the employee does not exist`() {
        val shift = newShift().copy(employeeId = Generator.randomInt(), startShift = now, endShift = now.plus(1, ChronoUnit.MINUTES))

        shiftTestClient.upsertShifts(listOf(shift)).expectStatus().isNotFound
    }

    @Test
    fun `When a shift in the batch is invalid, all the shifts are rejected`() {
        val employee = createEmployee(newEmployee())
        val validShift = newShift().copy(employeeId = employee.id, startShift = now, endShift = now.plus(30, ChronoUnit.MINUTES))
        val invalidShifts = listOf(
            newShift().copy(employeeId = employee.id, startShift = now.minus(1, ChronoUnit.MINUTES), endShift = now.plus(1, ChronoUnit.MINUTES)), // left
            newShift().copy(employeeId = employee.id, startShift = now.plus(1, ChronoUnit.MINUTES), endShift = now.plus(2, ChronoUnit.MINUTES)), // middle
            newShift().copy(employeeId = employee.id, startShift = now.plus(29, ChronoUnit.MINUTES), endShift = now.plus(31, ChronoUnit.MINUTES)), // right
        )
        val createValidShift = shiftTestClient.upsertShifts(listOf(validShift))
        createValidShift.expectStatus().isOk

        // Is rejected regardless if inserted in batch or not
        for (invalidShift in invalidShifts) {
            shiftTestClient.upsertShifts(listOf(invalidShift)).expectStatus().isEqualTo(HttpStatus.CONFLICT)
        }
        shiftTestClient.upsertShifts(invalidShifts).expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    private fun createEmployee(employee: Employee): Employee =
        employeeTestClient.createEmployee(employee).run {
            expectStatus().isCreated
            employeeTestClient.extractEmployee(this)
        }
}
