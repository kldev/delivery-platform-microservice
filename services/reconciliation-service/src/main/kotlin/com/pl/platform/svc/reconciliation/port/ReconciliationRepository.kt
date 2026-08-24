package com.pl.platform.svc.reconciliation.port

import com.pl.platform.svc.reconciliation.domain.Reconciliation
import java.util.UUID

interface ReconciliationRepository {

    fun findBySettlementId(settlementId: UUID): Reconciliation?
    fun findByDeliveryId(deliveryId: UUID): Reconciliation?

    fun findByPaymentId(paymentId: UUID): Reconciliation?

    fun create(reconciliation: Reconciliation)
    fun update(reconciliation: Reconciliation)
}