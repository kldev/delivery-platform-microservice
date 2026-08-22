package com.pl.platform.svc.delivery.application.command

import java.math.BigDecimal
import java.util.UUID

data class CreateDeliveryCommand(
    val pickupAddress: String,
    val deliveryAddress: String,
    var distanceKm: BigDecimal = BigDecimal.ZERO,
)