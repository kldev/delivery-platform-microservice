package com.pl.platform.svc.rest

import com.pl.platform.common.rest.ApiError
import com.pl.platform.common.rest.ApiValidationError
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ApiValidationError> {

        val errors = ex.bindingResult
            .fieldErrors
            .map {
                ApiValidationError.ValidationError(
                    field = it.field,
                    message = it.defaultMessage
                )
            }

        return ResponseEntity.badRequest()
            .body(
                ApiValidationError(
                    message = "Request validation failed",
                    errors = errors
                )
            )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException
    ): ResponseEntity<ApiError> =
        ResponseEntity.badRequest()
            .body(ApiError(ex.message))

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(
        ex: EntityNotFoundException
    ): ResponseEntity<ApiError> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiError(ex.message))

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException
    ): ResponseEntity<ApiError> =
        ResponseEntity.badRequest()
            .body(ApiError("Invalid request parameter"))

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(
        ex: HttpMessageNotReadableException
    ): ResponseEntity<ApiError> =
        ResponseEntity.badRequest()
            .body(ApiError("Invalid request body"))

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        ex: Exception
    ): ResponseEntity<ApiError> {

        log.error("Unexpected error", ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiError("Internal server error"))
    }

    companion object {
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }
}