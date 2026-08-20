package com.pl.platform.svc.delivery.adapter.rest.request

import com.pl.platform.svc.delivery.application.command.CreateDeliveryCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateDeliveryRequest(

    @field:NotNull
    val driverId: UUID?,

    @field:NotBlank
    @field:Size(max = 500)
    val pickupAddress: String,

    @field:NotBlank
    @field:Size(max = 500)
    val deliveryAddress: String
) {

    fun toCommand(): CreateDeliveryCommand =
        CreateDeliveryCommand(
            driverId = requireNotNull(driverId),
            pickupAddress = pickupAddress,
            deliveryAddress = deliveryAddress
        )
}