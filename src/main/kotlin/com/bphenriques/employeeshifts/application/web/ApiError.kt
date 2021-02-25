package com.bphenriques.employeeshifts.application.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

enum class ApiError(val code: String, val message: String, val httpStatus: HttpStatus) {
    UNEXPECTED_ERROR("010000", "An unexpected error has occurred. Contact Support.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("010001", "Bad request.", HttpStatus.BAD_REQUEST),
    EMPLOYEE_NOT_FOUND("010002", "Employee Not found.", HttpStatus.NOT_FOUND)
}

fun ApiError.toResponseEntity(throwable: Throwable): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(httpStatus.value())
    .body(ErrorResponse(code = code, message = message))

data class ErrorResponse(
    val code: String,
    val message: String
)
