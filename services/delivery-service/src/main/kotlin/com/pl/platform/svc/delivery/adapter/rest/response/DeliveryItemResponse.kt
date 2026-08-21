package com.pl.platform.svc.delivery.adapter.rest.response

import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import java.math.BigDecimal
import java.util.UUID

data class DeliveryItemResponse(   val id: UUID,
                                   var driverId: UUID?,
                                   val pickupAddress: String,
                                   val deliveryAddress: String,
                                   val price: BigDecimal,
                                   val status: DeliveryStatus)
{
    companion object {
        fun from(deliver: Delivery): DeliveryItemResponse {
            return DeliveryItemResponse( id = deliver.id.value,
                driverId = deliver.driverId,
                pickupAddress = deliver.pickupAddress,
                deliveryAddress = deliver.deliveryAddress,
                price = deliver.price,
                status = deliver.status)
        }
    }
}