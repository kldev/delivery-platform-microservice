package com.pl.platform.common.messaging.event.delivery

import java.util.UUID

data class DeliveryPickedUpEvent(
    override val deliveryId: UUID,
) : DeliveryEvent(
    deliveryId = deliveryId,
    eventType = DeliveryEventType.PICKED_UP.value
)