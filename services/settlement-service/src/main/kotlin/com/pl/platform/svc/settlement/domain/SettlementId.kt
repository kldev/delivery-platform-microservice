package com.pl.platform.svc.settlement.domain

import java.util.UUID

@JvmInline
value class SettlementId(
    val value: UUID
) {
    companion object {
        fun new(): SettlementId =
            SettlementId(UUID.randomUUID())
    }
}