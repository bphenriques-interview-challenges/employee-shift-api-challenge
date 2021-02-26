package com.bphenriques.employeeshifts.employee

import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.employee.service.EmployeeService
import com.bphenriques.employeeshifts.testhelper.EmployeeTestClient
import com.bphenriques.test.Generator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureWebTestClient
class EmployeeApiErrorTests {

    @Autowired
    private lateinit var employeeTestClient: EmployeeTestClient

    @MockkBean
    private lateinit var employeeService: EmployeeService

    @Test
    fun `GET employee id - It returns 404 when the employee does not exist`() {
        // Arrange
        val employeeId = Generator.randomInt()
        coEvery { employeeService.getEmployee(employeeId) } throws EmployeeNotFoundException(employeeId)

        // Act
        val response = employeeTestClient.getEmployee(employeeId)

        // Verify
        response.expectStatus().isNotFound

        coVerify { employeeService.getEmployee(employeeId) }
        confirmVerified(employeeService)
    }

    // TODO
    fun `POST employee - It returns 409 when the address is already in-use`() {
    }

    // TODO
    fun `POST employee - It returns 400 when the the fields exceed its maximum size`() {
    }
    // There are other test scenarios (e.g., BAD_REQUEST), however it is given by spring-boot.
}
