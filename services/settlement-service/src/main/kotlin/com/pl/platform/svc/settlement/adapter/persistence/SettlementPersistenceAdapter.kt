package com.pl.platform.svc.settlement.adapter.persistence

import com.pl.platform.svc.settlement.domain.Settlement
import com.pl.platform.svc.settlement.port.SettlementRepository
import org.springframework.stereotype.Component

@Component
class SettlementPersistenceAdapter(
    private val repository: SpringDataSettlementRepository,
) : SettlementRepository {

    override fun create(
        settlement: Settlement,
    ) {
        repository.save(
            SettlementJpaEntity.create(settlement)
        )
    }

    override fun getAll(): List<Settlement> =
        repository.findAll()
            .map(SettlementJpaEntity::toDomain)
}