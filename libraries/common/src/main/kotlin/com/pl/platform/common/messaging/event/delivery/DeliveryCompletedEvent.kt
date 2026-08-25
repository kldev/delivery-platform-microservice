package com.pl.platform.common.messaging.event.delivery

import java.math.BigDecimal
import java.util.*

data class DeliveryCompletedEvent(
    override val deliveryId: UUID,
    val driverId: UUID,
    val driverFullName: String,
    val distanceKm: BigDecimal,
    val price: BigDecimal,
    val currency: String = "PLN",
) : DeliveryEvent(deliveryId = deliveryId,
    eventType = DeliveryEventType.COMPLETED.value)