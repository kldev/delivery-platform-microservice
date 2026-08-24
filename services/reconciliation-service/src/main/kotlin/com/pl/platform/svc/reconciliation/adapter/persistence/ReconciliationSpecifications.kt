package com.pl.platform.svc.reconciliation.adapter.persistence

import com.pl.platform.svc.reconciliation.application.query.GetReconciliationQuery
import org.springframework.data.jpa.domain.Specification
import java.util.UUID

object ReconciliationSpecifications {

    fun build(
        query: GetReconciliationQuery,
    ): Specification<ReconciliationJpaEntity> {
        return listOfNotNull(
            query.deliveryId
                ?.let(::deliveryIdEquals),
            query.paymentId
                ?.let(::paymentIdEquals),
            query.settlementId
                ?.let(::settlementIdEquals),
            query.externalTransactionId
                ?.takeIf { it.isNotBlank() }
                ?.let(::externalTransactionIdLike),
        ).fold(
            Specification { _, _, _ -> null }
        ) { result, specification ->
            result.and(specification)
        }
    }


    private fun deliveryIdEquals(
        deliveryId: UUID,
    ): Specification<ReconciliationJpaEntity> =
        Specification { root, _, cb ->
            cb.equal(
                root.get<UUID>("deliveryId"),
                deliveryId,
            )
        }

    private fun paymentIdEquals(
        paymentId: UUID,
    ): Specification<ReconciliationJpaEntity> =
        Specification { root, _, cb ->
            cb.equal(
                root.get<UUID>("paymentId"),
                paymentId,
            )
        }

    private fun settlementIdEquals(
        settlementId: UUID,
    ): Specification<ReconciliationJpaEntity> =
        Specification { root, _, cb ->
            cb.equal(
                root.get<UUID>("settlementId"),
                settlementId,
            )
        }

    private fun externalTransactionIdLike(
        externalTransaction: String,
    ): Specification<ReconciliationJpaEntity> =
        Specification { root, _, cb ->
            cb.like(
                root.get<String>("externalTransactionId"),
                "%$externalTransaction%"
            )
        }

}