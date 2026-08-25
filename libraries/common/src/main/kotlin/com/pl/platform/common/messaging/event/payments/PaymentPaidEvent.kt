package com.pl.platform.common.messaging.event.payments

import com.pl.platform.common.messaging.event.Event
import java.time.Instant
import java.util.UUID

data class PaymentPaidEvent(
    override val deliveryId: UUID,
    override val paymentId: UUID
) : PaymentEvent(
    paymentId = paymentId,
    deliveryId= deliveryId,
    eventType = PaymentEventType.Paid.value
)