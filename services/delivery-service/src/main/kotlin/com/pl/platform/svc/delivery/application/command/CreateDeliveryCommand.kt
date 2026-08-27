package com.pl.platform.svc.delivery.application.command

import java.math.BigDecimal
import java.util.UUID

data class CreateDeliveryCommand(
    val pickupAddress: String,
    val deliveryAddress: String,
    val distanceKm: BigDecimal = BigDecimal.ZERO,
    val currency: String = "PLN"
)