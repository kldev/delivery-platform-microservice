package com.pl.platform.svc.delivery.adapter.rest

import com.pl.platform.svc.delivery.adapter.rest.request.CreateDeliveryRequest
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryCreateResponse
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryItemResponse
import com.pl.platform.svc.delivery.application.command.AssignDriverCommand
import com.pl.platform.svc.delivery.application.command.CancelDeliveryCommand
import com.pl.platform.svc.delivery.application.command.ConfirmDeliveryCommand
import com.pl.platform.svc.delivery.application.command.PickupDeliveryCommand
import com.pl.platform.svc.delivery.application.handler.AssignDriverHandler
import com.pl.platform.svc.delivery.application.handler.CancelDeliveryHandler
import com.pl.platform.svc.delivery.application.handler.ConfirmDeliveryHandler
import com.pl.platform.svc.delivery.application.handler.CreateDeliveryHandler
import com.pl.platform.svc.delivery.application.handler.GetAllDeliveryHandler
import com.pl.platform.svc.delivery.application.handler.PickupDeliveryHandler
import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.domain.DeliveryId
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Deliveries")
class DeliveryController(
    private val createDeliveryHandler: CreateDeliveryHandler,
    private val getAllDeliveryHandler: GetAllDeliveryHandler,
    private val confirmDeliveryHandler: ConfirmDeliveryHandler,
    private val assignDriverHandler: AssignDriverHandler,
    private val cancelDeliveryHandler: CancelDeliveryHandler,
    private val pickupDeliveryHandler: PickupDeliveryHandler
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateDeliveryRequest
    ): DeliveryCreateResponse =
        createDeliveryHandler.handle(
            request.toCommand()
        )

    @GetMapping
    fun getAll(): List<DeliveryItemResponse> =
        getAllDeliveryHandler.handle()

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{deliveryId}/confirm")
    fun confirmDelivery(@PathVariable deliveryId: UUID) = confirmDeliveryHandler.handle(
        ConfirmDeliveryCommand(deliveryId = DeliveryId(deliveryId))
    )

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{deliveryId}/assign")
    fun assignDriver(@PathVariable deliveryId: UUID, @RequestParam(required = false) driverId: UUID?)
    = assignDriverHandler.handle(
        AssignDriverCommand(deliveryId = DeliveryId(deliveryId),
            driverId = driverId)

    )

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{deliveryId}/cancel")
    fun cancelDelivery(@PathVariable deliveryId: UUID) = cancelDeliveryHandler.handle(
        CancelDeliveryCommand(deliveryId = DeliveryId(deliveryId))
    )

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{deliveryId}/pickup")
    fun pickUpDelivery(@PathVariable deliveryId: UUID) = pickupDeliveryHandler.handle(
        PickupDeliveryCommand(deliveryId = DeliveryId(deliveryId))
    )

}