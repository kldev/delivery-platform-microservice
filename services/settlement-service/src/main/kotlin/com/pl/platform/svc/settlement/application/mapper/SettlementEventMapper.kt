package com.pl.platform.svc.settlement.application.mapper

import com.pl.platform.svc.settlement.application.event.SettlementCreatedEvent
import com.pl.platform.svc.settlement.domain.Settlement
import java.time.Instant
import java.util.UUID

fun Settlement.toCreatedEvent(): SettlementCreatedEvent =
    SettlementCreatedEvent(
        eventId = UUID.randomUUID(),
        occurredAt = Instant.now(),
        aggregateId = id.value,
        deliveryId = deliveryId,
        driverId = driverId,
        driverFullName = driverFullName,
        deliveryAmount = deliveryAmount,
        currency = currency,
        basePercentage = basePercentage,
        nightPercentage = nightPercentage,
        weekendPercentage = weekendPercentage,
        distancePercentage = distancePercentage,
        totalPercentage = totalPercentage,
        driverAmount = driverAmount,
        distanceKm = distanceKm,
        completedAt = completedAt,
    )