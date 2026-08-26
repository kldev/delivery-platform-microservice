package com.pl.platform.svc.idempotency.adapter

import com.pl.platform.svc.idempotency.IdempotencyService
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import tools.jackson.databind.json.JsonMapper
import java.util.*

@ControllerAdvice
class IdempotencyResponseBodyAdvice(
    private val idempotencyService: IdempotencyService,
    private val objectMapper: JsonMapper
) : ResponseBodyAdvice<Any> {

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {

        return returnType.method
            ?.isAnnotationPresent(Idempotent::class.java)
            ?: false
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {

        val key = request.headers
            .getFirst("X-Idempotency-Key")
            ?.let(UUID::fromString)
            ?: return body

        val responseBody =
            objectMapper.writeValueAsString(body)

        val status =
            responseStatus(response)

        idempotencyService.complete(
            key = key,
            status = status,
            responseBody = responseBody
        )

        return body
    }

    private fun responseStatus(
        response: ServerHttpResponse
    ): Int {
        return (response as ServletServerHttpResponse)
            .servletResponse
            .status
    }
}