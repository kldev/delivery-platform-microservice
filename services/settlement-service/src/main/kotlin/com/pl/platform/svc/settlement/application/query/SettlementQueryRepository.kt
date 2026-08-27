package com.pl.platform.svc.settlement.application.query

import com.pl.platform.svc.common.AbstractJpaSliceQueryRepository
import com.pl.platform.svc.settlement.adapter.persistence.SettlementJpaEntity
import com.pl.platform.svc.settlement.adapter.persistence.SettlementSpecifications
import com.pl.platform.svc.settlement.adapter.rest.SettlementResponse
import com.pl.platform.svc.settlement.domain.Settlement
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class SettlementQueryRepository(
    entityManager: EntityManager,
) : AbstractJpaSliceQueryRepository<
        SettlementJpaEntity,
        GetSettlementsListQuery,
        SettlementResponse
        >(entityManager) {

    init {
        println("SettlementQueryRepository EntityManager = $entityManager")
    }

    override fun entityType() =
        SettlementJpaEntity::class.java

    override fun specification(
        query: GetSettlementsListQuery,
    ): Specification<SettlementJpaEntity> =
        SettlementSpecifications.build(query)

    override fun from(
        entity: SettlementJpaEntity,
    ): SettlementResponse =
        SettlementResponse.fromJpa(entity)
}