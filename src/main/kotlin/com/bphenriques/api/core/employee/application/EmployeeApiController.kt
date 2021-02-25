package com.bphenriques.api.core.employee.application

import com.bphenriques.api.core.employee.model.Employee
import com.bphenriques.api.core.employee.service.EmployeeService
import org.springframework.http.ResponseEntity
import javax.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("employee")
class EmployeeApiController(
    private val employeeService: EmployeeService
) {

    @PostMapping
    suspend fun addEmployee(@Valid @RequestBody employeeRequest: EmployeeRequest): ResponseEntity<EmployeeResponse> {
        val savedEmployee = employeeService.saveEmployee(employeeRequest.toEmployee())
        return ResponseEntity.ok(EmployeeResponse.fromEmployee(savedEmployee))
    }

    @GetMapping("/{id}")
    suspend fun getEmployee(@PathVariable id: Long): ResponseEntity<EmployeeResponse> {
        val fetchedEmployee = employeeService.getEmployee(id)
        return if (fetchedEmployee != null) {
            ResponseEntity.ok(EmployeeResponse.fromEmployee(fetchedEmployee))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class EmployeeRequest(
    val firstName: String,
    val lastName: String,
    val address: String
) {
    fun toEmployee(): Employee = Employee(
        firstName = firstName,
        lastName = lastName,
        address = address
    )
}

data class EmployeeResponse(
    val firstName: String,
    val lastName: String,
    val address: String
) {
    companion object {
        fun fromEmployee(employee: Employee): EmployeeResponse = EmployeeResponse(
            firstName = employee.firstName,
            lastName = employee.lastName,
            address = employee.address
        )
    }
}
