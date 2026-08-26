package com.pl.platform.svc.idempotency.adapter


import com.pl.platform.svc.idempotency.IdempotencyService
import com.pl.platform.svc.idempotency.exception.IdempotencyReplayException
import com.pl.platform.svc.idempotency.exception.InvalidIdempotencyKeyException
import com.pl.platform.svc.idempotency.exception.MissingIdempotencyKeyException
import com.pl.platform.svc.idempotency.model.IdempotencyCheckResult
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
import java.lang.reflect.Type
import java.util.*

@ControllerAdvice
class IdempotencyRequestBodyAdvice(
    private val idempotencyService: IdempotencyService
) : RequestBodyAdvice {

    override fun supports(
        methodParameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {

        return methodParameter.method
            ?.isAnnotationPresent(Idempotent::class.java)
            ?: false
    }

    override fun beforeBodyRead(
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): HttpInputMessage {

        val key = inputMessage.headers
            .getFirst("X-Idempotency-Key")
            ?.let {
                runCatching { UUID.fromString(it) }
                    .getOrElse {
                        throw InvalidIdempotencyKeyException()
                    }
            }
            ?: throw MissingIdempotencyKeyException()

        val body = inputMessage.body.readBytes()

        when (
            val result = idempotencyService.check(
                key = key,
                body = body
            )
        ) {

            is IdempotencyCheckResult.New -> {
                /*
                 * Continue normal Spring processing.
                 *
                 * Jackson will deserialize this body into
                 * CreateDeliveryRequest.
                 */
                return CachedHttpInputMessage(
                    inputMessage.headers,
                    body
                )
            }

            is IdempotencyCheckResult.Replay -> {
                /*
                 * Controller must NOT execute.
                 */
                throw IdempotencyReplayException(
                    status = result.status,
                    responseBody = result.body
                )
            }
        }
    }

    override fun afterBodyRead(
        body: Any,
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Any = body

    override fun handleEmptyBody(
        body: Any?,
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Any? = body
}