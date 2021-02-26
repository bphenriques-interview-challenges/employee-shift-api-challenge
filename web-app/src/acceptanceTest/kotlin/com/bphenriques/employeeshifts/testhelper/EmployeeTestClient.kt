package com.bphenriques.employeeshifts.testhelper

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.reactive.server.WebTestClient

@Component
class EmployeeTestClient(
    private val webTestClient: WebTestClient
) {

    fun createEmployee(employee: Employee): WebTestClient.ResponseSpec =
        webTestClient.post()
            .uri("/employee")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(JsonFixture.employee.createRequest(employee))
            .exchange()

    fun getEmployee(employeeId: Int): WebTestClient.ResponseSpec =
        webTestClient.get()
            .uri("/employee/$employeeId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

    fun updateEmployee(employeeId: Int, employee: Employee): WebTestClient.ResponseSpec =
        webTestClient.put()
            .uri("/employee/$employeeId")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(JsonFixture.employee.createRequest(employee))
            .exchange()

    fun deleteEmployee(employeeId: Int): WebTestClient.ResponseSpec =
        webTestClient.delete()
            .uri("/employee/$employeeId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
}
