package com.pl.platform.svc.settlement.domain

import java.math.BigDecimal
import java.util.UUID
import java.time.Instant

class Settlement(
    val id: SettlementId,
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
    val createdAt: Instant,
)