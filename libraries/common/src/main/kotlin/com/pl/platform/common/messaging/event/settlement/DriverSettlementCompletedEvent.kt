package com.pl.platform.common.messaging.event.settlement

import com.pl.platform.common.messaging.event.Event
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class DriverSettlementCompletedEvent(
    override val settlementId: UUID,
    val driverId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val driverFullName: String,
    val deliveryId: UUID,
) : SettlementEvent(
    settlementId = settlementId,
    eventType = SettlementEventType.DriverSettlementCompleted.value
)