package com.pl.platform.svc.settlement.application.event

import com.pl.platform.common.messaging.event.Event
import java.time.Instant
import java.util.UUID

abstract class SettlementEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    override val module: String = "settlement",
    override val aggregateId: UUID,
    override val eventType: String
) : Event