package com.bphenriques.employeeshifts.testhelper

import com.bphenriques.employeeshifts.domain.shift.model.Shift
import com.bphenriques.test.toISOFormat
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import java.time.Instant

@Component
class ShiftTestClient(
    private val webTestClient: WebTestClient
) {

    fun upsertShifts(shifts: List<Shift>): WebTestClient.ResponseSpec =
        upsertShifts(JsonFixture.upsertShiftsRequest(shifts))

    fun upsertShifts(rawJson: String): WebTestClient.ResponseSpec =
        webTestClient.post()
            .uri("/shifts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(rawJson)
            .exchange()

    fun findShiftsByEmployeeId(employeeIds: List<Int>): WebTestClient.ResponseSpec =
        webTestClient.get()
            .uri(
                UriComponentsBuilder.fromPath("/shifts/find")
                    .queryParam("employee_ids", employeeIds)
                    .build()
                    .toUri()
            )
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

    fun deleteShiftsId(shiftIds: List<Int>): WebTestClient.ResponseSpec =
        webTestClient.delete()
            .uri(
                UriComponentsBuilder.fromPath("/shifts")
                    .queryParam("ids", shiftIds)
                    .build()
                    .toUri()
            )
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

    fun extractShifts(responseSpec: WebTestClient.ResponseSpec): List<Shift> {
        val jsonTree = jacksonObjectMapper().readTree(responseSpec.returnResult(String::class.java).responseBody.blockFirst())
        return jsonTree.map { node ->
            Shift(
                id = node["id"].intValue(),
                employeeId = node["employee_id"].intValue(),
                startShift = Instant.parse(node["start_shift"].asText()),
                endShift = Instant.parse(node["end_shift"].asText())
            )
        }.toList()
    }

    object JsonFixture {
        fun upsertShiftsRequest(shifts: List<Shift>): String {
            val jsonShifts = shifts.map { shift ->
                if (shift.id == 0) {
                    createShiftJson(shift)
                } else {
                    updateShiftJson(shift)
                }
            }

            return "[${jsonShifts.joinToString(",") { it }}]"
        }

        private fun createShiftJson(shift: Shift) = """
            {
                "employee_id": ${shift.employeeId},
                "start_shift": "${shift.startShift.toISOFormat()}",
                "end_shift": "${shift.endShift.toISOFormat()}"
            }            
        """.trimIndent()

        private fun updateShiftJson(shift: Shift) = """
            {
                "id": ${shift.id},
                "employee_id": ${shift.employeeId},
                "start_shift": "${shift.startShift.toISOFormat()}",
                "end_shift": "${shift.endShift.toISOFormat()}"
            }            
        """.trimIndent()
    }
}
