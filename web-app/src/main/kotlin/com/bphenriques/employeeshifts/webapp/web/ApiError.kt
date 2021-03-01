package com.bphenriques.employeeshifts.webapp.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

enum class ApiError(val code: String, val message: String, val httpStatus: HttpStatus) {
    UNEXPECTED_ERROR("010000", "An unexpected error has occurred. Contact Support.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("010001", "Invalid request.", HttpStatus.BAD_REQUEST),
    EMPLOYEE_NOT_FOUND("010002", "Employee Not found.", HttpStatus.NOT_FOUND),
    EMPLOYEE_ADDRESS_ALREADY_IN_USE("010003", "There is already an employee using the provided address.", HttpStatus.CONFLICT),
    SHIFT_EMPLOYEE_NOT_FOUND("010004", "All shifts must be associated to an existent employee.", HttpStatus.NOT_FOUND),
    SHIFT_OVERLAPPING_SHIFTS("010005", "Shifts must not overlap one another.", HttpStatus.CONFLICT),
    SHIFT_INVALID_START_END_TIMES("010006", "Shifts start time must occur after end time.", HttpStatus.BAD_REQUEST),
}

fun ApiError.toResponseEntity(details: Any? = null): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(httpStatus.value())
    .body(ErrorResponse(code = code, message = message, details = details))

data class ErrorResponse(
    val code: String,
    val message: String,
    val details: Any?
)
