package com.pl.platform.svc.ledger.application.query

import com.pl.platform.svc.common.AbstractJpaSliceQueryRepository
import com.pl.platform.svc.ledger.adapter.persistence.LedgerTransactionJpaEntity
import com.pl.platform.svc.ledger.adapter.persistence.LedgerTransactionSpecifications
import com.pl.platform.svc.ledger.adapter.persistence.toDomain
import com.pl.platform.svc.ledger.domain.LedgerTransaction
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class LedgerTransactionQueryRepository(entityManager: EntityManager)
    :AbstractJpaSliceQueryRepository<LedgerTransactionJpaEntity, GetLedgerTransactionListQuery, LedgerTransaction>(entityManager) {
    override fun entityType(): Class<LedgerTransactionJpaEntity> =
        LedgerTransactionJpaEntity::class.java


    override fun specification(query: GetLedgerTransactionListQuery):
            Specification<LedgerTransactionJpaEntity> = LedgerTransactionSpecifications.build(query)

    override fun from(entity: LedgerTransactionJpaEntity): LedgerTransaction =
        entity.toDomain()
}