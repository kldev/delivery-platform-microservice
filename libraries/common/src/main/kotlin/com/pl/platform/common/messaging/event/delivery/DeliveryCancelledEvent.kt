package com.pl.platform.common.messaging.event.delivery

import java.util.UUID

data class DeliveryCancelledEvent(
    override val deliveryId: UUID,
) : DeliveryEvent(
    deliveryId = deliveryId,
    eventType = DeliveryEventType.CANCELLED.value
)