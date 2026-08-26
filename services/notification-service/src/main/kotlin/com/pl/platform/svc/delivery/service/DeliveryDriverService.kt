package com.pl.platform.svc.delivery.service

import com.pl.platform.svc.delivery.client.DeliveryClient
import com.pl.platform.svc.delivery.client.model.DriverResponse
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.time.Duration
import java.util.UUID

@ApplicationScoped
class DeliveryDriverService(
    @RestClient
    private val deliveryClient: DeliveryClient,
) {

    fun findDriverByDeliveryId(
        deliveryId: UUID,
    ): Uni<DriverResponse?> =
        deliveryClient
            .getDeliveries(
                status = null,
                deliveryId = deliveryId,
            )
            .onItem()
            .transformToUni { deliveries ->

                Log.info(
                    "Get deliveries ${deliveries.content.size} for $deliveryId"
                )

                val driverId = deliveries.content
                    .firstOrNull()
                    ?.driverId

                if (driverId == null) {
                    return@transformToUni Uni.createFrom()
                        .nullItem<DriverResponse>()
                }

                deliveryClient
                    .getSingleDrivers(driverId)
                    .onFailure().
                        retry().withBackOff(Duration.ofMillis(300))
                    .atMost(3)
                    .onItem()
                    .invoke { driver ->
                        Log.infof(
                            "Resolved driver for driverId=%s: %s",
                            driverId,
                            driver,
                        )
                    }
            }
}