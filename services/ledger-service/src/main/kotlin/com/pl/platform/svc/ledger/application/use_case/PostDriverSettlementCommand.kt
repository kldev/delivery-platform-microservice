package com.pl.platform.svc.ledger.application.use_case

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PostDriverSettlementCommand(
    val settlementId: UUID,
    val driverId: UUID,
    val driverFullName: String,
    val currency: String,
    val amount: BigDecimal,
    val occurredAt: Instant,
)