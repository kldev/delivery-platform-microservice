package com.pl.platform.svc.settlement.application.create_settlement

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class CreateSettlementCommand(
    val deliveryId: UUID,
    val driverId: UUID,
    val driverFullName: String,
    val deliveryAmount: BigDecimal,
    val currency: String,
    val distanceKm: BigDecimal,
    val completedAt: Instant,
)