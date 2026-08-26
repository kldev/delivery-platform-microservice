package com.pl.platform.svc.delivery.service

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.delivery.client.DeliveryClient
import com.pl.platform.svc.delivery.client.TooManyRequestsException
import com.pl.platform.svc.delivery.client.model.DeliveryItemResponse
import com.pl.platform.svc.delivery.client.model.DriverResponse
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.time.Duration
import java.util.UUID
import kotlin.random.Random

@ApplicationScoped
class DeliveryDriverService(
    @RestClient
    private val deliveryClient: DeliveryClient,
) {

    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_BACKOFF_MS = 500L
        private const val MAX_BACKOFF_MS = 5_000L
    }

    fun findDriverByDeliveryId(
        deliveryId: UUID,
    ): Uni<DriverResponse?> =
        getDeliveriesWithRetry(deliveryId)
            .onItem()
            .transformToUni { deliveries ->

                Log.infof(
                    "Found %d deliveries for deliveryId=%s",
                    deliveries.content.size,
                    deliveryId,
                )

                val driverId = deliveries.content
                    .firstOrNull()
                    ?.driverId
                    ?: return@transformToUni Uni.createFrom()
                        .nullItem<DriverResponse>()

                getDriverWithRetry(driverId)
            }

    private fun getDeliveriesWithRetry(
        deliveryId: UUID,
        attempt: Int = 0,
    ): Uni<SliceResponse<DeliveryItemResponse>> =
        deliveryClient
            .getDeliveries(
                status = null,
                deliveryId = deliveryId,
            )
            .onFailure(TooManyRequestsException::class.java)
            .recoverWithUni { failure ->

                if (attempt >= MAX_RETRIES) {
                    Log.warnf(
                        "Delivery API rate limit exceeded after %d retries, deliveryId=%s",
                        attempt,
                        deliveryId,
                    )

                    return@recoverWithUni Uni.createFrom()
                        .failure(failure)
                }

                val exception = failure as TooManyRequestsException

                retryAfter(
                    operation = "get deliveries",
                    identifier = deliveryId,
                    attempt = attempt,
                    retryAfterSeconds = exception.retryAfterSeconds,
                )
                    .flatMap {
                        getDeliveriesWithRetry(
                            deliveryId = deliveryId,
                            attempt = attempt + 1,
                        )
                    }
            }

    private fun getDriverWithRetry(
        driverId: UUID,
        attempt: Int = 0,
    ): Uni<DriverResponse> =
        deliveryClient
            .getSingleDrivers(driverId)
            .onFailure(TooManyRequestsException::class.java)
            .recoverWithUni { failure ->

                if (attempt >= MAX_RETRIES) {
                    Log.warnf(
                        "Driver API rate limit exceeded after %d retries, driverId=%s",
                        attempt,
                        driverId,
                    )

                    return@recoverWithUni Uni.createFrom()
                        .failure(failure)
                }

                val exception = failure as TooManyRequestsException

                retryAfter(
                    operation = "get driver",
                    identifier = driverId,
                    attempt = attempt,
                    retryAfterSeconds = exception.retryAfterSeconds,
                )
                    .flatMap {
                        getDriverWithRetry(
                            driverId = driverId,
                            attempt = attempt + 1,
                        )
                    }
            }
            .onItem()
            .invoke { driver ->
                Log.infof(
                    "Resolved driver for driverId=%s: %s",
                    driverId,
                    driver,
                )
            }

    private fun retryAfter(
        operation: String,
        identifier: UUID,
        attempt: Int,
        retryAfterSeconds: Long?,
    ): Uni<Unit> {

        val delay = calculateRetryDelay(
            attempt = attempt,
            retryAfterSeconds = retryAfterSeconds,
        )

        Log.debugf(
            "Remote API returned 429 for %s, retrying in %d ms, attempt=%d/%d, id=%s",
            operation,
            delay.toMillis(),
            attempt + 1,
            MAX_RETRIES,
            identifier,
        )

        return Uni.createFrom()
            .item(Unit)
            .onItem()
            .delayIt()
            .by(delay)
    }

    private fun calculateRetryDelay(
        attempt: Int,
        retryAfterSeconds: Long?,
    ): Duration {

        if (retryAfterSeconds != null) {
            return Duration.ofSeconds(
                retryAfterSeconds.coerceAtLeast(1)
            )
        }

        val exponentialDelay = (
                INITIAL_BACKOFF_MS * (1L shl attempt)
                ).coerceAtMost(MAX_BACKOFF_MS)

        val jitter = Random.nextLong(
            from = 0,
            until = (exponentialDelay / 2).coerceAtLeast(1),
        )

        return Duration.ofMillis(
            exponentialDelay + jitter,
        )
    }
}