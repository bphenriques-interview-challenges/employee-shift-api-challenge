package com.bphenriques.employeeshifts.webcontract

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.employee.service.EmployeeService
import com.bphenriques.employeeshifts.testhelper.Generator.randomLong
import com.bphenriques.employeeshifts.testhelper.Generator.uuid
import com.bphenriques.employeeshifts.testhelper.JsonFixture
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

// Contract Testing
@SpringBootTest
@AutoConfigureWebTestClient
class EmployeeApiControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var employeeService: EmployeeService

    @Test
    fun `POST employee - It accepts the employee and returns it with Status Code 200`() {
        // Arrange
        val expectedEmployee = Employee(
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )
        val expectedResponse = JsonFixture.employee.response(expectedEmployee)
        coEvery { employeeService.saveEmployee(expectedEmployee) } returns expectedEmployee

        // Act
        val response = webTestClient.post()
            .uri("/employee")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(JsonFixture.employee.request(expectedEmployee))
            .exchange()

        // Verify
        response.expectStatus().isOk
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
        response.expectBody().json(expectedResponse)

        coVerify { employeeService.saveEmployee(expectedEmployee) }
        confirmVerified(employeeService)
    }

    @Test
    fun `GET employee - When the employee exists, returns it with Status Code 200`() {
        // Arrange
        val employeeId = randomLong()
        val expectedEmployee = Employee(
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )
        val expectedResponse = JsonFixture.employee.response(expectedEmployee)
        coEvery { employeeService.getEmployee(employeeId) } returns expectedEmployee

        // Act
        val response = webTestClient.get()
            .uri("/employee/$employeeId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // Verify
        response.expectStatus().isOk
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
        response.expectBody().json(expectedResponse)

        coVerify { employeeService.getEmployee(employeeId) }
        confirmVerified(employeeService)
    }

    @Test
    fun `GET employee - It returns 404 when the employee does not exist`() {
        // Arrange
        val employeeId = randomLong()
        coEvery { employeeService.getEmployee(employeeId) } throws EmployeeNotFoundException(employeeId)

        // Act
        val response = webTestClient.get()
            .uri("/employee/$employeeId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // Verify
        response.expectStatus().isNotFound

        coVerify { employeeService.getEmployee(employeeId) }
        confirmVerified(employeeService)
    }
}
