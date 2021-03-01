package com.bphenriques.employeeshifts.infrastructure.psql

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeConstraintAlreadyInUseException
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeFieldsTooLargeException
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.testhelper.sql.SQLUtil
import com.bphenriques.employeeshifts.testhelper.sql.employee
import com.bphenriques.test.Generator.randomInt
import com.bphenriques.test.Generator.stringOfLength
import com.bphenriques.test.Generator.uuid
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class EmployeeRepositoryTest {

    @SpringBootApplication(scanBasePackageClasses = [EmployeeRepository::class])
    class App

    @Autowired
    private lateinit var subject: EmployeeRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setup() {
        SQLUtil.employee(jdbcTemplate).clear()
    }

    @Test
    fun `upsert - It saves new entities`() = runBlocking {
        val employee = Employee(
            id = 0,
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )

        val result = subject.upsert(employee)

        val numberRows = SQLUtil.employee(jdbcTemplate).count()
        Assertions.assertEquals(1, numberRows)
        Assertions.assertEquals(employee.copy(result.id), result)
    }

    @Test
    fun `upsert - It updates entities`() = runBlocking {
        val employee = Employee(
            id = 0,
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )
        val inserted = subject.upsert(employee)
        val numberRows = SQLUtil.employee(jdbcTemplate).count()
        Assertions.assertEquals(1, numberRows)
        val updatedEmployee = inserted.copy(address = uuid())

        subject.upsert(updatedEmployee)

        val storedUpdated = subject.get(updatedEmployee.id)
        Assertions.assertEquals(storedUpdated, updatedEmployee)
    }

    @Test
    fun `upsert - It throws EmployeeConstraintAlreadyInUseException if the address is already in use`() = runBlocking {
        val employee = Employee(
            id = 0,
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )
        subject.upsert(employee)
        Assertions.assertEquals(1, SQLUtil.employee(jdbcTemplate).count())
        val conflictingEmployee = Employee(
            id = 0,
            firstName = uuid(),
            lastName = uuid(),
            address = employee.address
        )

        val ex = assertThrows<EmployeeConstraintAlreadyInUseException> {
            subject.upsert(conflictingEmployee)
        }
        Assertions.assertEquals(ex.employee, conflictingEmployee)
        // Database state remains the same
        Assertions.assertEquals(1, SQLUtil.employee(jdbcTemplate).count())
    }

    @Test
    fun `upsert - It throws EmployeeFieldsTooLargeException if the any of the fields's legth is too large`() = runBlocking {
        val unusualEmployee = Employee(
            id = 0,
            firstName = stringOfLength(50),
            lastName = stringOfLength(50),
            address = stringOfLength(255)
        )
        subject.upsert(unusualEmployee)
        Assertions.assertEquals(1, SQLUtil.employee(jdbcTemplate).count())
        val violatingEmployees = listOf(
            unusualEmployee.copy(firstName = unusualEmployee.firstName + "z"),
            unusualEmployee.copy(firstName = unusualEmployee.lastName + "z"),
            unusualEmployee.copy(firstName = unusualEmployee.address + "z")
        )

        for (violatingEmployee in violatingEmployees) {
            val ex = assertThrows<EmployeeFieldsTooLargeException> {
                subject.upsert(violatingEmployee)
            }
            Assertions.assertEquals(ex.employee, violatingEmployee)
        }

        // Database state remains the same
        Assertions.assertEquals(1, SQLUtil.employee(jdbcTemplate).count())
    }

    @Test
    fun `get - It obtains the entity`() = runBlocking {
        val employee = Employee(
            id = 0,
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )
        val savedEntity = subject.upsert(employee)

        val loadedEntity = subject.get(savedEntity.id)

        Assertions.assertEquals(savedEntity, loadedEntity)
    }

    @Test
    fun `get - It throws EmployeeNotFoundException if the entity does not exist`() = runBlocking {
        val id = randomInt()
        val ex = assertThrows<EmployeeNotFoundException> {
            subject.get(id)
        }

        Assertions.assertEquals(ex.employeeId, id)
    }

    @Test
    fun `delete - It deletes the entity`() = runBlocking {
        val employee = Employee(
            id = 0,
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )
        val savedEntity = subject.upsert(employee)
        Assertions.assertEquals(1, SQLUtil.employee(jdbcTemplate).count())

        subject.delete(savedEntity.id)

        Assertions.assertEquals(0, SQLUtil.employee(jdbcTemplate).count())
    }
}
