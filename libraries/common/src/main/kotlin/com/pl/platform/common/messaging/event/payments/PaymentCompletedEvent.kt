package com.pl.platform.common.messaging.event.payments

import java.math.BigDecimal
import java.util.UUID

data class PaymentCompletedEvent(
    override val paymentId: UUID,
    override val deliveryId: UUID,
    val externalTransactionId: String,
    val amount: BigDecimal,
    val currency: String,
)
    : PaymentEvent(
        paymentId = paymentId,
        deliveryId = deliveryId,
        eventType = PaymentEventType.Completed.value
    ){
}