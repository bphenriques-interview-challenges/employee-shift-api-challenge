package com.bphenriques.employeeshifts.infrastructure.psql

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.shift.model.Shift
import com.bphenriques.employeeshifts.domain.shift.model.ShiftConstraintEmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.shift.model.ShiftConstraintEndBeforeOrAtStartException
import com.bphenriques.employeeshifts.domain.shift.model.ShiftConstraintOverlappingShiftsException
import com.bphenriques.employeeshifts.domain.shift.model.ShiftRowOperationException
import com.bphenriques.employeeshifts.testhelper.sql.SQLUtil
import com.bphenriques.employeeshifts.testhelper.sql.employee
import com.bphenriques.employeeshifts.testhelper.sql.shift
import com.bphenriques.test.Generator.newEmployee
import com.bphenriques.test.Generator.newShift
import com.bphenriques.test.Generator.randomInt
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest(properties = ["logging.level.org=DEBUG"])
class ShiftRepositoryTest {

    @SpringBootApplication(scanBasePackageClasses = [EmployeeRepository::class, ShiftRepository::class])
    class App

    @Autowired
    private lateinit var subject: ShiftRepository

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var employee1: Employee
    private lateinit var employee2: Employee
    private lateinit var employee3: Employee
    private lateinit var employee4: Employee
    private val now = Instant.now().truncatedTo(ChronoUnit.SECONDS) // Database has solely second precision.

    @BeforeEach
    fun setup() {
        SQLUtil.clearAll(jdbcTemplate)

        employee1 = runBlocking { employeeRepository.upsert(newEmployee()) }
        employee2 = runBlocking { employeeRepository.upsert(newEmployee()) }
        employee3 = runBlocking { employeeRepository.upsert(newEmployee()) }
        employee4 = runBlocking { employeeRepository.upsert(newEmployee()) }
    }

    @Test
    fun `upsert - It saves new entities`() = runBlocking {
        val shifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee2.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee3.id, startShift = now, endShift = now.plusSeconds(60))
        )

        val result = subject.upsert(shifts).toList()

        Assertions.assertEquals(3, SQLUtil.shift(jdbcTemplate).count())
        Assertions.assertEquals(shifts.toSet(), result.map { it.copy(id = 0) }.toSet()) // The ids are not relevant in this test
    }

    @Test
    fun `upsert - It updates entities`() = runBlocking {
        val shifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee2.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee3.id, startShift = now, endShift = now.plusSeconds(60))
        )

        val insertedShifts = subject.upsert(shifts).toList()
        Assertions.assertEquals(3, SQLUtil.shift(jdbcTemplate).count())

        val updatedShifts = subject.upsert(insertedShifts.map { it.copy(endShift = now.plusSeconds(120)) })
        val storedUpdated = subject.get(updatedShifts.map { it.id }).toList()
        Assertions.assertEquals(updatedShifts.toSet(), storedUpdated.toSet())
    }

    @Test
    fun `upsert - It does nothing when it provides an empty flow`() = runBlocking {
        val shifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee2.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee3.id, startShift = now, endShift = now.plusSeconds(60))
        )
        subject.upsert(shifts).toList()
        Assertions.assertEquals(3, SQLUtil.shift(jdbcTemplate).count())

        val result = subject.upsert(emptyList()).toList()
        Assertions.assertEquals(emptyList<Shift>(), result)
    }

    @Test
    fun `upsert - It accepts shifts one after another`() = runBlocking {
        val shifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(1)),
            newShift().copy(employeeId = employee1.id, startShift = now.plusSeconds(2), endShift = now.plusSeconds(3)),
        )

        val result = subject.upsert(shifts).toList()

        Assertions.assertEquals(2, SQLUtil.shift(jdbcTemplate).count())
        Assertions.assertEquals(shifts.toSet(), result.map { it.copy(id = 0) }.toSet()) // The ids are not relevant in this test
    }

    @Test
    fun `upsert - It throws ShiftConstraintEndBeforeOrAtStartException when end_shift is before or at start_shift`() = runBlocking {
        val invalidShifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now),
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.minusSeconds(1))
        )

        for (invalidShift in invalidShifts) {
            assertThrows<ShiftConstraintEndBeforeOrAtStartException> {
                subject.upsert(listOf(invalidShift)).toList()
            }
        }
    }

    @Test
    fun `upsert - It throws ShiftConstraintOverlappingShiftsException when the same employee has overlapping shifts`() = runBlocking {
        val existingShift = newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(30))
        val invalidShifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now.minusSeconds(1), endShift = now.plusSeconds(1)), // left
            newShift().copy(employeeId = employee1.id, startShift = now.plusSeconds(1), endShift = now.plusSeconds(2)), // middle
            newShift().copy(employeeId = employee1.id, startShift = now.plusSeconds(29), endShift = now.plusSeconds(31)), // right
        )

        subject.upsert(listOf(existingShift)).toList()
        Assertions.assertEquals(1, SQLUtil.shift(jdbcTemplate).count())
        for (invalidShift in invalidShifts) {
            assertThrows<ShiftConstraintOverlappingShiftsException> {
                subject.upsert(listOf(invalidShift)).toList()
            }
        }
    }

    @Test
    fun `upsert - It accepts overlapping shifts as long as they are different employees`() = runBlocking {
        val existingShift = newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(30))
        val validShifts = listOf(
            newShift().copy(employeeId = employee2.id, startShift = now.minusSeconds(1), endShift = now.plusSeconds(1)), // left
            newShift().copy(employeeId = employee3.id, startShift = now.plusSeconds(1), endShift = now.plusSeconds(2)), // middle
            newShift().copy(employeeId = employee4.id, startShift = now.plusSeconds(29), endShift = now.plusSeconds(31)), // right
        )

        subject.upsert(listOf(existingShift)).toList()
        Assertions.assertEquals(1, SQLUtil.shift(jdbcTemplate).count())
        for (invalidShift in validShifts) {
            subject.upsert(listOf(invalidShift)).toList()
        }
        Assertions.assertEquals(4, SQLUtil.shift(jdbcTemplate).count())
    }

    @Test
    fun `upsert - It transactionally updates all the shifts`() = runBlocking {
        val validShifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(30)),
            newShift().copy(employeeId = employee2.id, startShift = now.minusSeconds(1), endShift = now.plusSeconds(1)), // left
            newShift().copy(employeeId = employee3.id, startShift = now.plusSeconds(1), endShift = now.plusSeconds(2)), // middle
            newShift().copy(employeeId = employee4.id, startShift = now.plusSeconds(29), endShift = now.plusSeconds(31)), // right
        )
        val duplicateShift = newShift().copy(employeeId = employee4.id, startShift = now.plusSeconds(29), endShift = now.plusSeconds(31))

        assertThrows<ShiftRowOperationException> {
            subject.upsert((validShifts + duplicateShift)).toList()
        }
        Assertions.assertEquals(0, SQLUtil.shift(jdbcTemplate).count())
    }

    @Test
    fun `upsert - It throws ShiftConstraintEmployeeNotFoundException if the employee does not exist`() = runBlocking {
        val shift = newShift()

        val ex = assertThrows<ShiftConstraintEmployeeNotFoundException> {
            subject.upsert(listOf(shift)).toList()
        }
        Assertions.assertEquals(ex.shifts, listOf(shift))
    }

    @Test
    fun `get - It obtains the entities`() = runBlocking {
        val shifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee2.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee3.id, startShift = now, endShift = now.plusSeconds(60))
        )
        val insertedShifts = subject.upsert(shifts).toList()
        Assertions.assertEquals(3, SQLUtil.shift(jdbcTemplate).count())

        val loadedEntities = subject.get(insertedShifts.map { it.id }).toList()

        Assertions.assertEquals(insertedShifts, loadedEntities)
    }

    @Test
    fun `get - Returns an empty flow if no id is provided`() = runBlocking {
        val shifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee2.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee3.id, startShift = now, endShift = now.plusSeconds(60))
        )
        subject.upsert(shifts).toList()
        Assertions.assertEquals(3, SQLUtil.shift(jdbcTemplate).count())

        val loadedEntities = subject.get(emptyList()).toList()

        Assertions.assertEquals(emptyList<Shift>(), loadedEntities)
    }

    @Test
    fun `get - It returns empty flow if the entity does not does not exist`() = runBlocking {
        Assertions.assertEquals(emptyList<Shift>(), subject.get(listOf(randomInt())).toList())
    }

    @Test
    fun `findByEmployeeIds - Returns empty result when no id is provided`() = runBlocking {
        val result = subject.findByEmployeeIds(listOf()).toList()

        Assertions.assertEquals(emptyList<Shift>(), result)
    }

    @Test
    fun `findByEmployeeIds - Returns empty result when there is no matching employee`() = runBlocking {
        val shifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee2.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee3.id, startShift = now, endShift = now.plusSeconds(60))
        )
        subject.upsert(shifts).toList()

        val result = subject.findByEmployeeIds(listOf(employee4.id)).toList()

        Assertions.assertEquals(emptyList<Shift>(), result)
    }

    @Test
    fun `findByEmployeeIds - Returns the set of shifts associated to the employee`() = runBlocking {
        val expectedShifts = listOf(
            newShift().copy(employeeId = employee2.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee2.id, startShift = now.plusSeconds(60), endShift = now.plusSeconds(120)),
        )
        val otherShifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee3.id, startShift = now, endShift = now.plusSeconds(60))
        )
        val allSavedShifts = subject.upsert((expectedShifts + otherShifts)).toList()
        val savedEmployee2Shifts = allSavedShifts.filter { it.employeeId == employee2.id }.toSet()

        val result = subject.findByEmployeeIds(listOf(employee2.id)).toSet()

        Assertions.assertEquals(savedEmployee2Shifts, result)
    }

    @Test
    fun `delete - It deletes the entities`() = runBlocking {
        val shifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee2.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee3.id, startShift = now, endShift = now.plusSeconds(60))
        )
        val insertedShifts = subject.upsert(shifts).toList()
        Assertions.assertEquals(3, SQLUtil.shift(jdbcTemplate).count())

        subject.delete(insertedShifts.map { it.id })

        Assertions.assertEquals(0, SQLUtil.shift(jdbcTemplate).count())
    }

    @Test
    fun `delete - It deletes the shifts when the user is deleted`() = runBlocking {
        val shifts = listOf(
            newShift().copy(employeeId = employee1.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee2.id, startShift = now, endShift = now.plusSeconds(60)),
            newShift().copy(employeeId = employee3.id, startShift = now, endShift = now.plusSeconds(60))
        )
        subject.upsert(shifts).toList()
        Assertions.assertEquals(3, SQLUtil.shift(jdbcTemplate).count())

        SQLUtil.employee(jdbcTemplate).clear()

        Assertions.assertEquals(0, SQLUtil.employee(jdbcTemplate).count())
        Assertions.assertEquals(0, SQLUtil.shift(jdbcTemplate).count())
    }
}
