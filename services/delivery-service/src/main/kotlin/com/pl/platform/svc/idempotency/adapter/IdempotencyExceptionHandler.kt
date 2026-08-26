package com.pl.platform.svc.idempotency.adapter

import com.pl.platform.common.rest.ApiError
import com.pl.platform.svc.idempotency.exception.IdempotencyReplayException
import com.pl.platform.svc.idempotency.exception.IdempotencyRequestInProgressException
import com.pl.platform.svc.idempotency.exception.InvalidIdempotencyKeyException
import com.pl.platform.svc.idempotency.exception.MissingIdempotencyKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class IdempotencyExceptionHandler {
    @ExceptionHandler(IdempotencyReplayException::class)
    fun replay(
        exception: IdempotencyReplayException
    ): ResponseEntity<String> {

        return ResponseEntity
            .status(exception.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(exception.responseBody)
    }

    @ExceptionHandler(
        MissingIdempotencyKeyException::class
    )
    fun missingKey(): ResponseEntity<ApiError> {

        return ResponseEntity
            .badRequest()
            .body(
                ApiError(
                    message = "X-Idempotency-Key header is required"
                )
            )
    }

    @ExceptionHandler(
        InvalidIdempotencyKeyException::class
    )
    fun invalidKey(): ResponseEntity<ApiError> {

        return ResponseEntity
            .badRequest()
            .body(
                ApiError(
                    message = "X-Idempotency-Key must be a valid UUID"
                )
            )
    }

    @ExceptionHandler(
        IdempotencyKeyReuseException::class
    )
    fun keyReuse(): ResponseEntity<ApiError> {

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ApiError(
                    message = "Idempotency-Key was already used with a different request body"
                )
            )
    }

    @ExceptionHandler(
        IdempotencyRequestInProgressException::class
    )
    fun inUseReuse(): ResponseEntity<ApiError> {

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ApiError(
                    message = "\"Request with this Idempotency-Key is already in progress"
                )
            )
    }
}