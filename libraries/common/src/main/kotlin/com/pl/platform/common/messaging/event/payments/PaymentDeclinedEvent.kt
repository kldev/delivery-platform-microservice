package com.pl.platform.common.messaging.event.payments

import com.pl.platform.common.messaging.event.Event
import java.time.Instant
import java.util.UUID

data class PaymentDeclinedEvent(
    override val eventId: UUID,
    override val occurredAt: Instant,
    override val aggregateId: UUID,
    override val module: String,
    override val eventType: String,
    val deliveryId: UUID,
) : Event {
}