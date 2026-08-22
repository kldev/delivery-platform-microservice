package com.pl.platform.svc.settlement.port

import com.pl.platform.svc.settlement.domain.Settlement

interface SettlementRepository {

    fun create(settlement: Settlement)
    fun getAll(): List<Settlement>
}