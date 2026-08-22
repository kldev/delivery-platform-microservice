package com.pl.platform.svc.settlement.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SpringDataSettlementRepository :
    JpaRepository<SettlementJpaEntity, UUID> {

    fun findByDeliveryId(
        deliveryId: UUID,
    ): SettlementJpaEntity?

    fun findByDriverId(
        driverId: UUID,
    ): List<SettlementJpaEntity>
}