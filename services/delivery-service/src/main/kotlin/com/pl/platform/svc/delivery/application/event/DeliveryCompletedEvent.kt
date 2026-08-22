package com.pl.platform.svc.delivery.application.event

import java.math.BigDecimal
import java.util.*

data class DeliveryCompletedEvent(
    val deliveryId: UUID,
    val driverId: UUID,
    val driverFullName: String,
    val distanceKm: BigDecimal,
    val price: BigDecimal,
    val currency: String = "PLN",
) : DeliveryEvent(aggregateId = deliveryId,
    eventType = DeliveryEventType.COMPLETED.value)