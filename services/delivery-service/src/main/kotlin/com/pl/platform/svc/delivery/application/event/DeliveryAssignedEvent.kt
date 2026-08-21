package com.pl.platform.svc.delivery.application.event

import java.time.Instant
import java.util.UUID

data class DeliveryAssignedEvent(
    val deliveryId: UUID,
    val driverId: UUID,
) : DeliveryEvent(
    aggregateId= deliveryId,
    eventType = DeliveryEventType.ASSIGNED.value)