package com.pl.platform.svc.delivery.adapter.rest.response

import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import java.util.UUID

data class DeliveryResponse(
    val id: UUID,
    val driverId: UUID,
    val pickupAddress: String,
    val deliveryAddress: String,
    val status: DeliveryStatus
) {

    companion object {

        fun from(delivery: Delivery): DeliveryResponse =
            DeliveryResponse(
                id = delivery.id.value,
                driverId = delivery.driverId,
                pickupAddress = delivery.pickupAddress,
                deliveryAddress = delivery.deliveryAddress,
                status = delivery.status
            )
    }
}