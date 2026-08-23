package com.pl.platform.svc.ledger.application.query

import com.pl.platform.svc.common.AbstractJpaSliceQueryRepository
import com.pl.platform.svc.ledger.adapter.persistence.LedgerEntryJpaEntity
import com.pl.platform.svc.ledger.adapter.persistence.LedgerEntrySpecifications
import com.pl.platform.svc.ledger.adapter.persistence.toDomain
import com.pl.platform.svc.ledger.domain.LedgerEntry
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class LedgerEntryQueryRepository(entityManager: EntityManager) : AbstractJpaSliceQueryRepository<LedgerEntryJpaEntity,
        GetLedgerEntryListQuery,
        LedgerEntry>(entityManager) {

    override fun entityType(): Class<LedgerEntryJpaEntity> =
        LedgerEntryJpaEntity::class.java


    override fun specification(query: GetLedgerEntryListQuery):
            Specification<LedgerEntryJpaEntity> =
        LedgerEntrySpecifications.build(query)


    override fun from(entity: LedgerEntryJpaEntity): LedgerEntry =
        entity.toDomain()

}