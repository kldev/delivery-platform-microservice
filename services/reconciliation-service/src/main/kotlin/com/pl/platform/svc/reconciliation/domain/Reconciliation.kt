package com.pl.platform.svc.reconciliation.domain

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Reconciliation(
    val id: ReconciliationId = ReconciliationId(UUID.randomUUID()),
    val deliveryId: UUID,
    val settlementId: UUID? = null,
    val paymentId: UUID? = null,
    val externalTransactionId: String? = null,

    val expectedAmount: BigDecimal? = null,
    val actualAmount: BigDecimal? = null,
    val currency: String,

    val status: ReconciliationStatus = ReconciliationStatus.PENDING,

    val difference: BigDecimal? = null,

    val createdAt: Instant = Instant.now(),
    val reconciledAt: Instant? = null
) {

    fun applySettlement(
        settlementId: UUID,
        amount: BigDecimal,
        currency: String
    ): Reconciliation {

        require(this.currency == currency) {
            "Currency mismatch: $currency != ${this.currency}"
        }

        return copy(
            settlementId = settlementId,
            expectedAmount = amount
        ).reconcile()
    }

    fun applyPayment(
        paymentId: UUID,
        externalTransactionId: String,
        actualAmount: BigDecimal,
        currency: String
    ): Reconciliation {
        val difference = actualAmount.subtract(expectedAmount)

        require(this.currency == currency) {
            "Currency mismatch: $currency != ${this.currency}"
        }

        return copy(
            currency = currency,
            paymentId = paymentId,
            externalTransactionId = externalTransactionId,
            actualAmount = actualAmount,
            difference = difference,
            reconciledAt = Instant.now()
        ).reconcile()
    }

    fun markMissingPayment(): Reconciliation =
        copy(
            status = ReconciliationStatus.MISSING_PAYMENT
        )

    private fun reconcile(): Reconciliation {

        if (expectedAmount == null || actualAmount == null) {
            return copy(
                status = ReconciliationStatus.PENDING,
                difference = null,
                reconciledAt = null
            )
        }

        val difference = actualAmount.subtract(expectedAmount)

        return copy(
            difference = difference,
            status = if (difference.compareTo(BigDecimal.ZERO) == 0) {
                ReconciliationStatus.RECONCILED
            } else {
                ReconciliationStatus.DISCREPANCY
            },
            reconciledAt = Instant.now()
        )
    }
}