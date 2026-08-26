package com.pl.platform.svc.driver.adapter.rest

import com.pl.platform.svc.driver.adapter.rest.request.CreateDriverRequest
import com.pl.platform.svc.driver.adapter.rest.response.DriverResponse
import com.pl.platform.svc.driver.application.handler.CreateDriverHandler
import com.pl.platform.svc.driver.application.handler.GetAllDriversHandler
import com.pl.platform.svc.driver.application.handler.GetDriverHandler
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Drivers")
class DriverController(
    private val createDriverHandler: CreateDriverHandler,
    private val getAllDrivers: GetAllDriversHandler,
    private val getDriver: GetDriverHandler

) {
    @PostMapping
    fun create(@Valid @RequestBody request: CreateDriverRequest): DriverResponse =
        createDriverHandler.handle(request.toCommand())

    @GetMapping
    fun getAll(@RequestParam(required = false)  driverId: UUID? ) : List<DriverResponse> = getAllDrivers.handle(driverId)

    @GetMapping("/{driverId}")
    fun getDriver(@PathVariable()  driverId: UUID ) : DriverResponse? = getDriver.handle(driverId)
}


