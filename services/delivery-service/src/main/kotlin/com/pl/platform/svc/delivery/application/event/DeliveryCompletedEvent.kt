package com.pl.platform.svc.delivery.application.event

import java.time.Instant
import java.util.UUID

data class DeliveryCompletedEvent(
    val deliveryId: UUID,
    override val occurredAt: Instant
) : DeliveryEvent(aggregateId = deliveryId,
    eventType = DeliveryEventType.COMPLETED.value)