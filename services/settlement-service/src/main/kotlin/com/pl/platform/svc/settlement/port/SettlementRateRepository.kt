package com.pl.platform.svc.settlement.port

import com.pl.platform.svc.settlement.domain.SettlementRate

interface SettlementRateRepository {

    fun findActive(): List<SettlementRate>
}