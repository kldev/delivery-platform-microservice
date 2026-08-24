package com.pl.platform.svc.reconciliation.application.query
import com.pl.platform.svc.common.AbstractJpaSliceQueryRepository
import com.pl.platform.svc.reconciliation.adapter.persistence.ReconciliationJpaEntity
import com.pl.platform.svc.reconciliation.adapter.persistence.ReconciliationSpecifications
import com.pl.platform.svc.reconciliation.domain.Reconciliation
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class ReconciliationQueryRepository(entityManager: EntityManager): AbstractJpaSliceQueryRepository<
        ReconciliationJpaEntity,
        GetReconciliationQuery,
        Reconciliation>(entityManager) {
    override fun entityType(): Class<ReconciliationJpaEntity> = ReconciliationJpaEntity::class.java

    override fun specification(query: GetReconciliationQuery): Specification<ReconciliationJpaEntity> =
        ReconciliationSpecifications.build(query)

    override fun from(entity: ReconciliationJpaEntity): Reconciliation = entity.toDomain()

}