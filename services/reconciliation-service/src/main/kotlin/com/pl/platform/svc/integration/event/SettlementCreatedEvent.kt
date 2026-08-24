package com.pl.platform.svc.integration.event

import com.pl.platform.common.messaging.event.Event
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class SettlementCreatedEvent(
    val settlementId: UUID,
    val deliveryId: UUID,
    val driverId: UUID,
    val amount: BigDecimal,
    val currency: String,
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    override val aggregateId: UUID = settlementId,
    override val module: String = "settlement",
    override val eventType: String = "settlement.created"
) : Event