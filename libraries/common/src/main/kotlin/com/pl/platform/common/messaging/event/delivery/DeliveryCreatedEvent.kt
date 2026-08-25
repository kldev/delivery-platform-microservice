package com.pl.platform.common.messaging.event.delivery

import java.math.BigDecimal
import java.util.UUID


data class DeliveryCreatedEvent(
    override val deliveryId: UUID,
    var price: BigDecimal
) : DeliveryEvent(
    deliveryId = deliveryId,
    eventType = DeliveryEventType.CREATED.value)