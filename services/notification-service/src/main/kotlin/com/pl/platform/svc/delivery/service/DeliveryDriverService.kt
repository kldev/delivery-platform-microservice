package com.pl.platform.svc.delivery.service

import com.pl.platform.svc.delivery.client.DeliveryClient
import com.pl.platform.svc.delivery.client.model.DriverResponse
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.util.UUID

@ApplicationScoped
class DeliveryDriverService(
    @RestClient
    private val deliveryClient: DeliveryClient,
) {

    fun findDriverByDeliveryId(
        deliveryId: UUID,
    ): Uni<DriverResponse?> {
        val deliveries = deliveryClient.getDeliveries(
            status = null,
            deliveryId = deliveryId,
        )

        Log.info("Get deliveries ${deliveries.content.size} for $deliveryId")

        val driverId = deliveries.content
            .firstOrNull()
            ?.driverId
            ?: return Uni.createFrom().item { null as DriverResponse? }

        return Uni.createFrom().item {
            deliveryClient
                .getDrivers(driverId)
                .firstOrNull()
        }.onItem().invoke { driver ->
            Log.infof(
                "Resolved driver for driverId=%s: %s",
                driverId,
                driver,
            )
        }
    }
}