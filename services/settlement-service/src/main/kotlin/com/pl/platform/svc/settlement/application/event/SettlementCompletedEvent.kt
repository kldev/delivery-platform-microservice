package com.pl.platform.svc.settlement.application.event

import java.util.UUID

data class SettlementCompletedEvent(val id: UUID) : SettlementEvent(
    aggregateId = id,
    eventType = "settlement.completed",
) {
}