package com.pl.platform.svc.delivery.adapter.persistence

import com.pl.platform.svc.delivery.domain.DeliveryStatus


enum class DeliveryStatusJpa {
    CREATED,
    CONFIRMED,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED;

    fun toDomain(): DeliveryStatus =
        DeliveryStatus.valueOf(name)

    companion object {
        fun from(status: DeliveryStatus): DeliveryStatusJpa =
            valueOf(status.name)
    }
}