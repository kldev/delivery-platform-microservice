package com.pl.platform.common.messaging.event.delivery

import java.math.BigDecimal
import java.util.UUID

data class DeliveryConfirmedEvent(
    override val deliveryId: UUID,
    var price: BigDecimal,
    var currency: String = "PLN"
) : DeliveryEvent(deliveryId = deliveryId,
    eventType = DeliveryEventType.CONFIRMED.value
)