package com.pl.platform.svc.settlement.adapter.persistence

import com.pl.platform.svc.settlement.domain.SettlementRate
import com.pl.platform.svc.settlement.port.SettlementRateRepository
import org.springframework.stereotype.Component

@Component
class SettlementRatePersistenceAdapter(
    private val repository: SpringDataSettlementRateRepository,
) : SettlementRateRepository {

    override fun findActive(): List<SettlementRate> =
        repository
            .findAllByActiveTrue()
            .map(SettlementRateJpaEntity::toDomain)
}