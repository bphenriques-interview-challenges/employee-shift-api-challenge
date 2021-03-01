package com.bphenriques.employeeshifts.webapp.web

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class GlobalErrorHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationExceptions(ex: WebExchangeBindException): ResponseEntity<ErrorResponse> {
        val errorsGrouped = ex.bindingResult.allErrors.associate {
            val fieldError = it as FieldError
            fieldError.field to fieldError.defaultMessage
        }

        return ApiError.BAD_REQUEST.toResponseEntity(details = errorsGrouped)
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(ex: ServerWebInputException): ResponseEntity<ErrorResponse> {
        logger.warn(ex.message)
        return ApiError.BAD_REQUEST.toResponseEntity()
    }
}
