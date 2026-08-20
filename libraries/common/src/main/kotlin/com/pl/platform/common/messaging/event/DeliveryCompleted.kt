package com.pl.platform.common.messaging.event

import java.time.Instant
import java.util.UUID

data class DeliveryCompleted(
    val deliveryId: UUID,
    val driverId: UUID,
    val completedAt: Instant
) : Event