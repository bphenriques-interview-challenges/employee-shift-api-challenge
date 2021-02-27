package com.bphenriques.employeeshifts.testhelper

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
            .bodyValue(JsonFixture.createRequest(employee))
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
            .bodyValue(JsonFixture.createRequest(employee))
            .exchange()

    fun deleteEmployee(employeeId: Int): WebTestClient.ResponseSpec =
        webTestClient.delete()
            .uri("/employee/$employeeId")
            .exchange()

    fun extractEmployee(responseSpec: WebTestClient.ResponseSpec): Employee {
        val jsonTree = jacksonObjectMapper().readTree(responseSpec.returnResult(String::class.java).responseBody.blockFirst())
        return Employee(
            id = jsonTree["id"].intValue(),
            firstName = jsonTree["first_name"].asText(),
            lastName = jsonTree["last_name"].asText(),
            address = jsonTree["address"].asText()
        )
    }

    object JsonFixture {
        fun createRequest(employee: Employee) = """
            {
                "first_name": "${employee.firstName}",
                "last_name": "${employee.lastName}",
                "address": "${employee.address}"
            }
        """.trimIndent()
    }
}
