package com.pl.platform.svc.delivery.application.command

import java.util.UUID

data class CreateDeliveryCommand(
    val driverId: UUID,
    val pickupAddress: String,
    val deliveryAddress: String
)