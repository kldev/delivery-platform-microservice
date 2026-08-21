package com.pl.platform.svc.delivery.adapter.rest

import com.pl.platform.svc.delivery.adapter.rest.request.CreateDeliveryRequest
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryCreateResponse
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryItemResponse
import com.pl.platform.svc.delivery.application.handler.CreateDeliveryHandler
import com.pl.platform.svc.delivery.application.handler.GetAllDeliveryHandler
import com.pl.platform.svc.delivery.domain.Delivery
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Deliveries")
class DeliveryController(
    private val createDeliveryHandler: CreateDeliveryHandler,
    private val getAllDeliveryHandler: GetAllDeliveryHandler
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
    fun getAll():List<DeliveryItemResponse> =
        getAllDeliveryHandler.handle()

}