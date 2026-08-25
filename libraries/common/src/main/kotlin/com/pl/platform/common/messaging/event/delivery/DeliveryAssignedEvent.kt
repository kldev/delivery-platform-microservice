package com.pl.platform.common.messaging.event.delivery

import java.util.UUID

data class DeliveryAssignedEvent(
    override val deliveryId: UUID,
    val driverId: UUID,
) : DeliveryEvent(
    deliveryId,
    eventType = DeliveryEventType.ASSIGNED.value)