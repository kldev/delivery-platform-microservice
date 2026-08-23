package com.pl.platform.svc.integration.event

import com.pl.platform.common.messaging.event.Event
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class DeliveryCompletedEvent(
    override val eventId: UUID,
    override val occurredAt: Instant,
    override val aggregateId: UUID,
    override val module: String,
    override val eventType: String,
    val deliveryId: UUID,
    val driverId: UUID,
    val driverFullName: String,
    val distanceKm: BigDecimal,
    val price: BigDecimal,
    val currency: String
) : Event {
}