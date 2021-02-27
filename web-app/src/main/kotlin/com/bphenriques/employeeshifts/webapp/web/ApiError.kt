package com.bphenriques.employeeshifts.webapp.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

enum class ApiError(val code: String, val message: String, val httpStatus: HttpStatus) {
    UNEXPECTED_ERROR("010000", "An unexpected error has occurred. Contact Support.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("010001", "Invalid request.", HttpStatus.BAD_REQUEST),
    EMPLOYEE_NOT_FOUND("010002", "Employee Not found.", HttpStatus.NOT_FOUND),
    EMPLOYEE_CONFLICTING_OPERATION("010003", "Conflicting employee operation", HttpStatus.CONFLICT),
    SHIFT_CONFLICTING_OPERATION("010004", "Conflicting shift operation", HttpStatus.CONFLICT)
}

fun ApiError.toResponseEntity(details: Any? = null): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(httpStatus.value())
    .body(ErrorResponse(code = code, message = message, details = details))

data class ErrorResponse(
    val code: String,
    val message: String,
    val details: Any?
)
