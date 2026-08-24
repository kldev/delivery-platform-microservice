package com.pl.platform.svc.delivery.application.query

import com.pl.platform.svc.delivery.adapter.persistence.DeliveryStatusDurationJpaEntity
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class DeliveryStatusDurationItem(val id: UUID,
                                      val deliveryId: UUID,
                                      val status: DeliveryStatus,
                                      val previousStatus: DeliveryStatus?,
                                      val statusFrom: Instant?,
                                      val statusTo: Instant?,
                                      val durationSeconds: BigDecimal) {
    companion object{
        fun from(entity: DeliveryStatusDurationJpaEntity) : DeliveryStatusDurationItem
        = DeliveryStatusDurationItem(id = entity.id,
            deliveryId = entity.deliveryId,
            status = entity.status,
            previousStatus = entity.previousStatus,
            statusFrom = entity.statusFrom,
            statusTo = entity.statusTo,
            durationSeconds = entity.durationSeconds)
    }
}