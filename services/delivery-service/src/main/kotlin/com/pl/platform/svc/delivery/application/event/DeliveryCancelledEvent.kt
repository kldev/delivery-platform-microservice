package com.pl.platform.svc.delivery.application.event

import java.time.Instant
import java.util.UUID

data class DeliveryCancelledEvent(
    val deliveryId: UUID,
) : DeliveryEvent(aggregateId = deliveryId,
    eventType = DeliveryEventType.CANCELLED.value
)