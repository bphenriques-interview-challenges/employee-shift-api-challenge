package com.bphenriques.employeeshifts.webapp.web

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeConstraintAlreadyInUseException
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeFieldsTooLargeException
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.employee.service.EmployeeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI
import javax.validation.Valid
import javax.validation.constraints.Size

@RestController
@RequestMapping("employee")
@Tag(name = "Employee", description = "Employee API")
class EmployeeApiController(
    private val employeeService: EmployeeService
) {

    @Operation(summary = "Create Employee")
    @PostMapping
    suspend fun create(@Valid @RequestBody employeeRequest: CreateEmployeeRequest): ResponseEntity<EmployeeResponse> {
        val savedEmployee = employeeService.upsert(employeeRequest.toEmployee())
        return ResponseEntity
            .created(URI.create("/employee/${savedEmployee.id}"))
            .body(EmployeeResponse.fromEmployee(savedEmployee))
    }

    @Operation(summary = "Get Employee")
    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Int): ResponseEntity<EmployeeResponse> {
        val fetchedEmployee = employeeService.get(id)
        return ResponseEntity.ok(EmployeeResponse.fromEmployee(fetchedEmployee))
    }

    @Operation(summary = "Update Employee")
    @PutMapping("/{id}")
    suspend fun update(@PathVariable id: Int, @Valid @RequestBody employeeRequest: UpdateEmployeeRequest): ResponseEntity<EmployeeResponse> {
        val updatedEmployee = employeeService.upsert(employeeRequest.toEmployee(id))
        return if (id == 0) {
            ResponseEntity
                .created(URI.create("/employee/${updatedEmployee.id}"))
                .body(EmployeeResponse.fromEmployee(updatedEmployee))
        } else {
            ResponseEntity.ok(EmployeeResponse.fromEmployee(updatedEmployee))
        }
    }

    @Operation(summary = "Delete Employee")
    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: Int): ResponseEntity<Unit> {
        employeeService.delete(id)
        return ResponseEntity.ok().build()
    }
}

@RestControllerAdvice
class EmployeeApiControllerErrorHandling {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(EmployeeNotFoundException::class)
    fun handleEmployeeNotFoundException(ex: EmployeeNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn(ex.message, ex)
        return ApiError.EMPLOYEE_NOT_FOUND.toResponseEntity()
    }

    @ExceptionHandler(EmployeeConstraintAlreadyInUseException::class)
    fun handleConflictingEmployeeException(ex: EmployeeConstraintAlreadyInUseException): ResponseEntity<ErrorResponse> {
        logger.warn(ex.message, ex)
        return ApiError.EMPLOYEE_ADDRESS_ALREADY_IN_USE.toResponseEntity()
    }

    @ExceptionHandler(EmployeeFieldsTooLargeException::class)
    fun handleEmployeeFieldsTooLargeException(ex: EmployeeFieldsTooLargeException): ResponseEntity<ErrorResponse> {
        logger.warn(ex.message, ex)
        return ApiError.BAD_REQUEST.toResponseEntity()
    }
}

@Schema(name = "Create Employee Request")
data class CreateEmployeeRequest(
    @field:Size(min = 1, max = Employee.MAX_LENGTH_FIRST_NAME) val firstName: String,
    @field:Size(min = 1, max = Employee.MAX_LENGTH_LAST_NAME) val lastName: String,
    @field:Size(min = 1, max = Employee.MAX_LENGTH_ADDRESS) val address: String
) {
    fun toEmployee(): Employee = Employee(
        id = 0,
        firstName = firstName,
        lastName = lastName,
        address = address
    )
}

@Schema(name = "Update Employee Request")
data class UpdateEmployeeRequest(
    @field:Size(min = 1, max = Employee.MAX_LENGTH_FIRST_NAME) val firstName: String,
    @field:Size(min = 1, max = Employee.MAX_LENGTH_LAST_NAME) val lastName: String,
    @field:Size(min = 1, max = Employee.MAX_LENGTH_ADDRESS) val address: String
) {
    fun toEmployee(employeeId: Int): Employee = Employee(
        id = employeeId,
        firstName = firstName,
        lastName = lastName,
        address = address
    )
}

@Schema(name = "Employee")
data class EmployeeResponse(
    val id: Int,
    @field:Size(min = 1, max = Employee.MAX_LENGTH_FIRST_NAME) val firstName: String,
    @field:Size(min = 1, max = Employee.MAX_LENGTH_LAST_NAME) val lastName: String,
    @field:Size(min = 1, max = Employee.MAX_LENGTH_ADDRESS) val address: String
) {
    companion object {
        fun fromEmployee(employee: Employee): EmployeeResponse = EmployeeResponse(
            id = employee.id,
            firstName = employee.firstName,
            lastName = employee.lastName,
            address = employee.address
        )
    }
}
