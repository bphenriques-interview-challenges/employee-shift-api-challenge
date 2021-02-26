package com.bphenriques.employeeshifts.infrastructure.psql

import com.bphenriques.employeeshifts.infrastructure.configuration.FlywayConfiguration
import com.bphenriques.employeeshifts.testhelper.sql.SQLUtil
import com.bphenriques.employeeshifts.testhelper.sql.employee
import com.bphenriques.test.Generator.uuid
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient

@SpringBootApplication(scanBasePackageClasses = [FlywayConfiguration::class])
class TestApplication

@SpringBootTest
class EmployeeRepositoryTest {

    @Autowired
    private lateinit var subject: EmployeeRepository

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @BeforeEach
    fun setup() {
        SQLUtil.employee(databaseClient).clear()
    }

    @Test
    fun `It is correctly configured as it does not throw an error when booting`() = runBlocking {
        Assertions.assertEquals(0, subject.count())
    }

    @Test
    fun `save - It saves the entity`() = runBlocking {
        val employeeRow = EmployeeRow(
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )

        subject.save(employeeRow)

        val numberRows = SQLUtil.employee(databaseClient).count()
        Assertions.assertEquals(1, numberRows)
        // No need to validate the entity itself as we assume the correct behavior of Spring Boot's save method.
    }

    @Test
    fun `get - It obtains the entity`() = runBlocking {
        val employeeRow = EmployeeRow(
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )
        val savedEntity = subject.save(employeeRow)
        val numberRows = SQLUtil.employee(databaseClient).count()

        Assertions.assertEquals(1, numberRows)

        val loadedEntity = subject.findById(savedEntity.id)
        Assertions.assertEquals(savedEntity, loadedEntity)
    }
}
