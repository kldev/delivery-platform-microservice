package com.pl.platform.svc.delivery.adapter.rest.response

import com.pl.platform.svc.delivery.adapter.persistence.DeliveryJpaEntity
import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class DeliveryItemResponse(
    val id: UUID,
    var driverId: UUID?,
    val pickupAddress: String,
    val deliveryAddress: String,
    val price: BigDecimal,
    val status: DeliveryStatus,
    val distanceKm: BigDecimal,
    val currency: String = "PLN",
    val createdAt: Instant,
    var updatedAt: Instant?
)
{
    companion object {
        fun from(deliver: DeliveryJpaEntity): DeliveryItemResponse {
            return DeliveryItemResponse( id = deliver.id,
                driverId = deliver.driverId,
                pickupAddress = deliver.pickupAddress,
                deliveryAddress = deliver.deliveryAddress,
                price = deliver.price,
                status = deliver.status.toDomain(),
                distanceKm = deliver.distanceKm,
                currency = deliver.currency ?: "PLN",
                createdAt = deliver.createdAt,
                updatedAt = deliver.updatedAt)
        }
    }
}