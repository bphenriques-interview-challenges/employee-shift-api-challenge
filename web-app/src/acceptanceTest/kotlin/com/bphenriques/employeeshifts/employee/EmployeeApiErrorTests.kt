package com.bphenriques.employeeshifts.employee

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.testhelper.EmployeeTestClient
import com.bphenriques.employeeshifts.testhelper.SQLUtil
import com.bphenriques.employeeshifts.testhelper.newEmployee
import com.bphenriques.test.Generator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.r2dbc.core.DatabaseClient

@SpringBootTest
@AutoConfigureWebTestClient
class EmployeeApiErrorTests {

    @Autowired
    private lateinit var employeeTestClient: EmployeeTestClient

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @BeforeEach
    fun setup() {
        SQLUtil.clearAll(databaseClient)
    }

    @Test
    fun `GET employee id - It returns 404 when the employee does not exist`() {
        // Arrange
        val employeeId = Generator.randomInt()

        // Act
        val response = employeeTestClient.getEmployee(employeeId)

        // Verify
        response.expectStatus().isNotFound
    }

    @Test
    fun `POST employee - It returns 409 when the address is being used`() {
        val employee = newEmployee()
        val createResponse = employeeTestClient.createEmployee(employee)
        createResponse.expectStatus().isCreated

        val conflictingEmployee = newEmployee().copy(address = employee.address)
        val conflictResponse = employeeTestClient.createEmployee(conflictingEmployee)
        conflictResponse.expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun `POST employee - It returns 400 when the the fields exceed its maximum size`() {
        val unusualEmployee = Employee(
            id = 0,
            firstName = Generator.stringOfLength(50),
            lastName = Generator.stringOfLength(50),
            address = Generator.stringOfLength(255)
        )
        val createResponse = employeeTestClient.createEmployee(unusualEmployee)
        createResponse.expectStatus().isCreated
        val violatingEmployees = listOf(
            unusualEmployee.copy(firstName = unusualEmployee.firstName + "z"),
            unusualEmployee.copy(firstName = unusualEmployee.lastName + "z"),
            unusualEmployee.copy(firstName = unusualEmployee.address + "z")
        )

        for (violatingEmployee in violatingEmployees) {
            val conflictResponse = employeeTestClient.createEmployee(violatingEmployee)
            conflictResponse.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
        }
    }
}
