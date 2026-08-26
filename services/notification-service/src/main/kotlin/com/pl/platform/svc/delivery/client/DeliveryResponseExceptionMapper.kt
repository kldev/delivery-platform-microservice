package com.pl.platform.svc.delivery.client


import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper

class DeliveryResponseExceptionMapper :
    ResponseExceptionMapper<RuntimeException> {

    override fun toThrowable(response: Response): RuntimeException? {
        if (response.status != Response.Status.TOO_MANY_REQUESTS.statusCode) {
            return null
        }

        val retryAfter = response
            .headers
            .getFirst("Retry-After")
            ?.toString()
            ?.toLongOrNull()

        return TooManyRequestsException(
            retryAfterSeconds = retryAfter
        )
    }

    override fun getPriority(): Int = 500
}