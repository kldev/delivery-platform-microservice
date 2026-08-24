package com.pl.platform.svc.reconciliation.adapter.persistence


import com.pl.platform.svc.reconciliation.domain.Reconciliation
import com.pl.platform.svc.reconciliation.port.ReconciliationRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ReconciliationRepositoryAdapter(
    private val jpaRepository: SpringDataReconciliationRepository
) : ReconciliationRepository {

    override fun findBySettlementId(
        settlementId: UUID
    ): Reconciliation? =
        jpaRepository
            .findBySettlementId(settlementId)
            ?.toDomain()

    override fun findByDeliveryId(
        deliveryId: UUID
    ): Reconciliation? =
        jpaRepository
            .findByDeliveryId(deliveryId)
            ?.toDomain()

    override fun findByPaymentId(
        paymentId: UUID
    ): Reconciliation? =
        jpaRepository
            .findByPaymentId(paymentId)
            ?.toDomain()

    override fun create(
        reconciliation: Reconciliation
    ) {
        jpaRepository.save(
            ReconciliationJpaEntity.fromDomain(reconciliation)
        )
    }

    override fun update(
        reconciliation: Reconciliation
    ) {
        val entity = jpaRepository.findById(
            reconciliation.id.value
        ).orElseThrow {
            IllegalStateException(
                "Reconciliation ${reconciliation.id.value} not found"
            )
        }

        entity.updateFrom(reconciliation)

        jpaRepository.saveAndFlush(entity)
    }
}