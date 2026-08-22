package com.pl.platform.svc.settlement.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataSettlementRateRepository :
    JpaRepository<SettlementRateJpaEntity, String> {

    fun findAllByActiveTrue(): List<SettlementRateJpaEntity>
}