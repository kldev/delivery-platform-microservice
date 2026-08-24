package com.pl.platform.svc.integration.event

import com.pl.platform.common.messaging.event.Event
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PaymentCompletedEvent(
    val paymentId: UUID,
    val externalTransactionId: String,
    val deliveryId: UUID,
    val amount: BigDecimal,
    val currency: String,
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    override val aggregateId: UUID = paymentId,
    override val module: String = "payment",
    override val eventType: String = "payment.completed"
) : Event