package com.pl.platform.svc.delivery.adapter.rest

import com.pl.platform.svc.delivery.adapter.rest.request.CreateDeliveryRequest
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryCreateResponse
import com.pl.platform.svc.delivery.application.DeliveryService
import com.pl.platform.svc.delivery.application.command.DeliveryAction
import com.pl.platform.svc.idempotency.adapter.Idempotent
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
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

    @Idempotent
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create delivery",
        description = """
            Creates a new delivery.
            
            The X-Idempotency-Key header must be a unique UUID.
            Reusing the same key with the same request body returns
            the original response without creating another delivery.
            
            Reusing the same key with a different request body returns 409 Conflict.
        """,
        parameters = [
            Parameter(
                name = "X-Idempotency-Key",
                `in` = ParameterIn.HEADER,
                required = true,
                description = "Unique idempotency key"
            )
        ]
    )
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