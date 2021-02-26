package com.bphenriques.employeeshifts.employee

import com.bphenriques.employeeshifts.testhelper.EmployeeTestClient
import com.bphenriques.employeeshifts.testhelper.SQLUtil
import com.bphenriques.employeeshifts.testhelper.newEmployee
import com.bphenriques.test.Generator.uuid
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ManagingUsersTest {

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
        // Create Employees for entropy (ensures lack of fixed results) and then test a specific one.
        repeat(5) { employeeTestClient.createEmployee(newEmployee()).expectStatus().isCreated }

        val employeeA = newEmployee()
        val createResponse = employeeTestClient.createEmployee(employeeA)
        createResponse.expectStatus().isCreated
        createResponse.expectHeader().contentType(MediaType.APPLICATION_JSON)
        createResponse.expectBody()
            .jsonPath(".first_name").isEqualTo(employeeA.firstName)
            .jsonPath(".last_name").isEqualTo(employeeA.lastName)
            .jsonPath(".address").isEqualTo(employeeA.address)
        val employeeId = consumeBodyAndExtractId(createResponse)
        createResponse.expectHeader().location("/employee/$employeeId")

        val getResponse = employeeTestClient.getEmployee(employeeId)
        getResponse.expectStatus().isOk
        getResponse.expectHeader().contentType(MediaType.APPLICATION_JSON)
        getResponse.expectBody()
            .jsonPath(".id").isEqualTo(employeeId)
            .jsonPath(".first_name").isEqualTo(employeeA.firstName)
            .jsonPath(".last_name").isEqualTo(employeeA.lastName)
            .jsonPath(".address").isEqualTo(employeeA.address)

        val updateEmployeeAAddress = employeeA.copy(address = uuid())

        val updateResponse = employeeTestClient.updateEmployee(employeeId, updateEmployeeAAddress)
        updateResponse.expectStatus().isOk
        updateResponse.expectHeader().contentType(MediaType.APPLICATION_JSON)
        updateResponse.expectBody()
            .jsonPath(".id").isEqualTo(employeeId)
            .jsonPath(".first_name").isEqualTo(updateEmployeeAAddress.firstName)
            .jsonPath(".last_name").isEqualTo(updateEmployeeAAddress.lastName)
            .jsonPath(".address").isEqualTo(updateEmployeeAAddress.address)

        val deleteResponse = employeeTestClient.deleteEmployee(employeeId)
        deleteResponse.expectStatus().isOk

        val getResponseAfterDeletion = employeeTestClient.getEmployee(employeeId)
        getResponseAfterDeletion.expectStatus().isNotFound
    }

    private fun consumeBodyAndExtractId(responseSpec: WebTestClient.ResponseSpec): Int {
        val jsonNode = responseSpec.returnResult(JsonNode::class.java).responseBody.blockFirst()!!
        return (jsonNode["id"] as IntNode).asInt()
    }
}
