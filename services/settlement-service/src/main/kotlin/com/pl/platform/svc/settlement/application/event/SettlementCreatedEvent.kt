package com.pl.platform.svc.settlement.application.event

import com.pl.platform.common.messaging.event.Event
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class SettlementCreatedEvent(
    override val eventId: UUID,
    override val occurredAt: Instant,
    override val aggregateId: UUID,
    override val module: String = "settlement",
    override val eventType: String = "settlement.created",

    val deliveryId: UUID,
    val driverId: UUID,
    val driverFullName: String,
    val deliveryAmount: BigDecimal,
    val currency: String,
    val basePercentage: BigDecimal,
    val nightPercentage: BigDecimal,
    val weekendPercentage: BigDecimal,
    val distancePercentage: BigDecimal,
    val totalPercentage: BigDecimal,
    val driverAmount: BigDecimal,
    val distanceKm: BigDecimal,
    val completedAt: Instant,
) : Event

