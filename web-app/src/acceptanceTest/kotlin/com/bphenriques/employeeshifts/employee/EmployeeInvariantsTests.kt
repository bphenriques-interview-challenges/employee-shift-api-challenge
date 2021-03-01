package com.bphenriques.employeeshifts.employee

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.testhelper.EmployeeTestClient
import com.bphenriques.employeeshifts.testhelper.SQLUtil
import com.bphenriques.test.Generator.newEmployee
import com.bphenriques.test.Generator.stringOfLength
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class EmployeeInvariantsTests {

    @Autowired
    private lateinit var employeeTestClient: EmployeeTestClient

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setup() {
        SQLUtil.clearAll(jdbcTemplate)
    }

    @Test
    fun `POST employee - It returns 409 when the address is being used`() {
        val employee = newEmployee()
        employeeTestClient.createEmployee(employee).expectStatus().isCreated

        val conflictingEmployee = newEmployee().copy(address = employee.address)
        employeeTestClient.createEmployee(conflictingEmployee).expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun `POST employee - It returns 400 when the the fields exceed its maximum size`() {
        val unusualEmployee = Employee(
            id = 0,
            firstName = stringOfLength(50),
            lastName = stringOfLength(50),
            address = stringOfLength(255)
        )
        employeeTestClient.createEmployee(unusualEmployee).expectStatus().isCreated

        val violatingEmployees = listOf(
            unusualEmployee.copy(firstName = unusualEmployee.firstName + "z"),
            unusualEmployee.copy(firstName = unusualEmployee.lastName + "z"),
            unusualEmployee.copy(firstName = unusualEmployee.address + "z")
        )
        for (violatingEmployee in violatingEmployees) {
            employeeTestClient.createEmployee(violatingEmployee).expectStatus().isBadRequest
        }
    }
}
