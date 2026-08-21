package com.pl.platform.svc.delivery.adapter.rest

import com.pl.platform.svc.delivery.adapter.rest.request.CreateDeliveryRequest
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryCreateResponse
import com.pl.platform.svc.delivery.application.handler.CreateDeliveryHandler
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/deliveries")
class DeliveryController(
    private val createDeliveryHandler: CreateDeliveryHandler
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateDeliveryRequest
    ): DeliveryCreateResponse =

        createDeliveryHandler.handle(
            request.toCommand()
        )

}