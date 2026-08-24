package com.pl.platform.svc.reconciliation.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataReconciliationRepository : JpaRepository<ReconciliationJpaEntity, UUID> {
    fun findBySettlementId(settlementId: UUID): ReconciliationJpaEntity?
    fun findByDeliveryId(deliveryId: UUID): ReconciliationJpaEntity?
    fun findByPaymentId(paymentId: UUID): ReconciliationJpaEntity?
}