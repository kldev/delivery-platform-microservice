package com.pl.platform.svc.delivery.adapter.rest

import com.pl.platform.svc.delivery.adapter.rest.request.CreateDeliveryRequest
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryCreateResponse
import com.pl.platform.svc.delivery.application.DeliveryService
import com.pl.platform.svc.delivery.application.command.DeliveryAction
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Deliveries")
class DeliveryCommandController(
    private val deliveryService: DeliveryService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateDeliveryRequest
    ): DeliveryCreateResponse =
        deliveryService.create(request.toCommand())

    @PutMapping("/{deliveryId}/{action}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun executeAction(
        @PathVariable deliveryId: UUID,
        @PathVariable action: DeliveryAction,
        @RequestParam(required = false) driverId: UUID?
    ) {
        when (action) {
            DeliveryAction.CONFIRM ->
                deliveryService.confirm(deliveryId)

            DeliveryAction.ASSIGN ->
                deliveryService.assign(deliveryId, driverId)

            DeliveryAction.CANCEL ->
                deliveryService.cancel(deliveryId)

            DeliveryAction.PICKUP ->
                deliveryService.pickup(deliveryId)

            DeliveryAction.START ->
                deliveryService.start(deliveryId)

            DeliveryAction.COMPLETE ->
                deliveryService.complete(deliveryId)
        }
    }
}