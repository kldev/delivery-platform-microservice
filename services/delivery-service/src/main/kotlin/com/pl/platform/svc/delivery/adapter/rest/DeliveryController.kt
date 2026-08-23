package com.pl.platform.svc.delivery.adapter.rest

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.delivery.adapter.rest.request.CreateDeliveryRequest
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryCreateResponse
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryItemResponse
import com.pl.platform.svc.delivery.application.command.*
import com.pl.platform.svc.delivery.application.handler.*
import com.pl.platform.svc.delivery.application.query.DeliveryQueryRepository
import com.pl.platform.svc.delivery.application.query.GetDeliveryQuery
import com.pl.platform.svc.delivery.domain.DeliveryId
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Deliveries")
class DeliveryController(
    private val createDeliveryHandler: CreateDeliveryHandler,
    private val confirmDeliveryHandler: ConfirmDeliveryHandler,
    private val assignDriverHandler: AssignDriverHandler,
    private val cancelDeliveryHandler: CancelDeliveryHandler,
    private val pickupDeliveryHandler: PickupDeliveryHandler,
    private val startDeliveryHandler: StartDeliveryHandler,
    private val completeDeliveryHandler: CompleteDeliveryHandler,
    private val deliveryQueryRepository: DeliveryQueryRepository
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
    fun getAll(@RequestParam(required = false) status: DeliveryStatus?,
               @RequestParam(required = false) deliveryId: UUID?,
               @RequestParam(required = false, defaultValue = "100")@Max(500) size: Int,
               @RequestParam(required = false, defaultValue = "0") page: Int,): SliceResponse<DeliveryItemResponse> {

        val result = deliveryQueryRepository.search(
            GetDeliveryQuery(status, deliveryId), PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.DESC, "createdAt")
            )
        );

        val response = SliceResponse(
            content = result.content.map(DeliveryItemResponse::from).toList(),
            hasNext = result.hasNext
        )

        return response

    }

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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{deliveryId}/start")
    fun startDelivery(@PathVariable deliveryId: UUID) = startDeliveryHandler.handle(
        StartDeliveryCommand(deliveryId = DeliveryId(deliveryId))
    )

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{deliveryId}/complete")
    fun completeDelivery(@PathVariable deliveryId: UUID) = completeDeliveryHandler.handle(
        CompleteDeliveryCommand(deliveryId = DeliveryId(deliveryId))
    )


}