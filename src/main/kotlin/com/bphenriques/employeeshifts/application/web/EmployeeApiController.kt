package com.bphenriques.employeeshifts.application.web

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.employee.service.EmployeeService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.validation.Valid

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
        return ResponseEntity.ok(EmployeeResponse.fromEmployee(fetchedEmployee))
    }
}

@RestControllerAdvice
class EmployeeApiControllerErrorHandling {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(EmployeeNotFoundException::class)
    fun handleEmployeeNotFoundException(ex: EmployeeNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn(ex.message)
        return ApiError.EMPLOYEE_NOT_FOUND.toResponseEntity(ex)
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
