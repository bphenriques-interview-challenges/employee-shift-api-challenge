package com.bphenriques.employeeshifts.employee

import com.bphenriques.employeeshifts.testhelper.EmployeeTestClient
import com.bphenriques.employeeshifts.testhelper.SQLUtil
import com.bphenriques.test.Generator
import com.bphenriques.test.Generator.newEmployee
import com.bphenriques.test.Generator.uuid
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CRUDEmployeeTest {

    @Autowired
    private lateinit var employeeTestClient: EmployeeTestClient

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @BeforeEach
    fun setup() {
        SQLUtil.clearAll(databaseClient)
    }

    @Test
    fun `Create, read, update, and delete employee details`() {
        val employee = newEmployee()

        // Create
        val createdEmployee = employeeTestClient.createEmployee(employee).run {
            expectStatus().isCreated
            expectHeader().contentType(MediaType.APPLICATION_JSON)

            val result = employeeTestClient.extractEmployee(this)
            expectHeader().location("/employee/${result.id}")
            result
        }
        Assertions.assertEquals(employee, createdEmployee.copy(id = 0))

        // Read
        val fetchedEmployee = employeeTestClient.getEmployee(createdEmployee.id).run {
            expectStatus().isOk
            expectHeader().contentType(MediaType.APPLICATION_JSON)
            employeeTestClient.extractEmployee(this)
        }
        Assertions.assertEquals(createdEmployee, fetchedEmployee)

        // Update
        val updatedEmployee = fetchedEmployee.copy(address = uuid())
        val updateEmployeeResponse = employeeTestClient.updateEmployee(createdEmployee.id, updatedEmployee).run {
            expectStatus().isOk
            expectHeader().contentType(MediaType.APPLICATION_JSON)
            employeeTestClient.extractEmployee(this)
        }
        Assertions.assertEquals(updatedEmployee, updateEmployeeResponse)
        Assertions.assertEquals(
            updatedEmployee,
            employeeTestClient.getEmployee(createdEmployee.id).run { employeeTestClient.extractEmployee(this) }
        )

        // Delete
        employeeTestClient.deleteEmployee(createdEmployee.id).expectStatus().isOk
        employeeTestClient.getEmployee(createdEmployee.id).expectStatus().isNotFound
    }

    @Test
    fun `GET employee id - It returns 404 when the employee does not exist`() {
        val employeeId = Generator.randomInt()

        employeeTestClient.getEmployee(employeeId).expectStatus().isNotFound
    }
}
