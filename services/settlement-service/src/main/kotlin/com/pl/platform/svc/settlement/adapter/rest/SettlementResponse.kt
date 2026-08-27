package com.pl.platform.svc.settlement.adapter.rest

import com.pl.platform.svc.settlement.adapter.persistence.SettlementJpaEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class SettlementResponse(
    val id: UUID,
    val deliveryId: UUID,
    val driverId: UUID,
    val driverFullName: String,
    val deliveryAmount: BigDecimal,
    val currency: String,
    val basePercentage: BigDecimal,
    val nightPercentage: BigDecimal,
    val weekendPercentage: BigDecimal,
    val distancePercentage: BigDecimal,
    val totalPercentage: BigDecimal,
    val driverAmount: BigDecimal,
    val distanceKm: BigDecimal,
    val completedAt: Instant,
    val createdAt: Instant
)
{
    companion object {
          fun fromJpa(entity: SettlementJpaEntity): SettlementResponse
                = SettlementResponse(id = entity.id,
            deliveryId = entity.deliveryId,
            driverId = entity.driverId,
            driverFullName = entity.driverFullName,
            deliveryAmount = entity.deliveryAmount,
            currency = entity.currency,
            basePercentage = entity.basePercentage,
            nightPercentage = entity.nightPercentage,
            weekendPercentage = entity.weekendPercentage,
            distancePercentage = entity.distancePercentage,
            totalPercentage = entity.totalPercentage,
            driverAmount = entity.driverAmount,
            distanceKm = entity.distanceKm,
            completedAt = entity.completedAt,
            createdAt = entity.createdAt
        )
    }
}