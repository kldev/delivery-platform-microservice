package com.pl.platform.svc.delivery.application.query

import com.pl.platform.svc.delivery.domain.DeliveryStatus
import java.util.UUID

data class GetDeliveryQuery(val status: DeliveryStatus?, val deliveryId: UUID?, val driverId: UUID? = null) {
    companion object {
        fun empty() = GetDeliveryQuery(null, null, null)
    }
}