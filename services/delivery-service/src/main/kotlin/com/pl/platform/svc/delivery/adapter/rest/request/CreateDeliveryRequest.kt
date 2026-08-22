package com.pl.platform.svc.delivery.adapter.rest.request

import com.pl.platform.svc.delivery.application.command.CreateDeliveryCommand
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

data class CreateDeliveryRequest(
    @field:NotBlank
    @field:Size(max = 500)
    val pickupAddress: String,

    @field:NotBlank
    @field:Size(max = 500)
    val deliveryAddress: String,

    @field:Max(15000)
    val distanceKm: BigDecimal
) {

    fun toCommand(): CreateDeliveryCommand =
        CreateDeliveryCommand(
            pickupAddress = pickupAddress,
            deliveryAddress = deliveryAddress,
            distanceKm = distanceKm
        )
}