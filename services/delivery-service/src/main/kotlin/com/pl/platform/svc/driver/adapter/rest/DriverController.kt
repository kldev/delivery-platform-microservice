package com.pl.platform.svc.driver.adapter.rest

import com.pl.platform.svc.driver.adapter.rest.request.CreateDriverRequest
import com.pl.platform.svc.driver.adapter.rest.response.DriverResponse
import com.pl.platform.svc.driver.application.handler.CreateDriverHandler
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Deliveries")
class DriverController(
    private val createDriverHandler: CreateDriverHandler
) {
    @PostMapping
    fun create(@Valid @RequestBody request: CreateDriverRequest): DriverResponse =
        createDriverHandler.handle(request.toCommand())

}