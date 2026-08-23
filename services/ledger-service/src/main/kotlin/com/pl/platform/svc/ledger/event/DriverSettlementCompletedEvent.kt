package com.pl.platform.svc.ledger.event

import com.pl.platform.common.messaging.event.Event
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class DriverSettlementCompletedEvent(
    val settlementId: UUID,
    val driverId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val driverFullName: String,
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    override val module: String = "settlement",
    override val eventType: String = "driver.settlement.completed",

    override val aggregateId: UUID = settlementId,

) : Event