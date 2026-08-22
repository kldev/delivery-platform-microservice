package com.pl.platform.svc.delivery.application.event

import java.math.BigDecimal
import java.util.UUID

data class DeliveryConfirmedEvent(
    val deliveryId: UUID,
    var price: BigDecimal,
    var currency: String = "PLN"
) : DeliveryEvent(aggregateId = deliveryId,
    eventType = DeliveryEventType.CONFIRMED.value
)