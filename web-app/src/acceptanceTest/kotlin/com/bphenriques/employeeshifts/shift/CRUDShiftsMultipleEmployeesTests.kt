package com.bphenriques.employeeshifts.shift

import com.bphenriques.employeeshifts.domain.employee.model.Employee
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
class CRUDShiftsMultipleEmployeesTests {

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
    fun `Create, read, update, and delete shifts for multiple employees`() {
        val employee1 = createEmployee(newEmployee())
        val employee2 = createEmployee(newEmployee())
        val employee3 = createEmployee(newEmployee())
        val shift1Employee1 = newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plus(1, ChronoUnit.MINUTES))
        val shift2Employee1 = newShift().copy(employeeId = employee1.id, startShift = now.plus(1, ChronoUnit.MINUTES), endShift = now.plus(2, ChronoUnit.MINUTES))
        val shift1Employee2 = newShift().copy(employeeId = employee2.id, startShift = now, endShift = now.plus(1, ChronoUnit.MINUTES))
        val shift1Employee3 = newShift().copy(employeeId = employee3.id, startShift = now, endShift = now.plus(1, ChronoUnit.MINUTES))
        val allShifts = listOf(shift1Employee1, shift2Employee1, shift1Employee2, shift1Employee3)

        // Create
        val createdShifts = shiftTestClient.upsertShifts(allShifts).run {
            expectStatus().isOk
            expectHeader().contentType(MediaType.APPLICATION_JSON)
            shiftTestClient.extractShifts(this)
        }
        Assertions.assertEquals(allShifts.truncatedToMinute(), createdShifts.map { it.copy(id = 0) })

        // Find
        val getByEmployee1And2 = shiftTestClient.findShiftsByEmployeeId(listOf(employee1.id, employee2.id)).run {
            expectStatus().isOk
            expectHeader().contentType(MediaType.APPLICATION_JSON)
        }
        Assertions.assertEquals(
            createdShifts.filter { it.employeeId == employee1.id || it.employeeId == employee2.id }.toSet(),
            shiftTestClient.extractShifts(getByEmployee1And2).toSet()
        )

        // Update
        val moveAllShiftsUp = createdShifts.map {
            it.copy(
                startShift = it.startShift.plus(1, ChronoUnit.HOURS),
                endShift = it.endShift.plus(1, ChronoUnit.HOURS)
            )
        }
        shiftTestClient.upsertShifts(moveAllShiftsUp).run {
            expectStatus().isOk
            expectHeader().contentType(MediaType.APPLICATION_JSON)
        }
        val updatedShifts = shiftTestClient.findShiftsByEmployeeId(moveAllShiftsUp.map { it.employeeId }).run {
            shiftTestClient.extractShifts(this)
        }
        Assertions.assertEquals(
            moveAllShiftsUp.toSet(),
            updatedShifts.toSet()
        )

        // Delete
        shiftTestClient.deleteShiftsId(updatedShifts.filter { it.employeeId == employee1.id }.map { it.id }).run {
            expectStatus().isOk
        }
        Assertions.assertEquals(
            updatedShifts.filterNot { it.employeeId == employee1.id },
            shiftTestClient.extractShifts(shiftTestClient.findShiftsByEmployeeId(listOf(employee1.id, employee2.id, employee3.id)))
        )
    }

    private fun createEmployee(employee: Employee): Employee =
        employeeTestClient.createEmployee(employee).run {
            expectStatus().isCreated
            employeeTestClient.extractEmployee(this)
        }
}
