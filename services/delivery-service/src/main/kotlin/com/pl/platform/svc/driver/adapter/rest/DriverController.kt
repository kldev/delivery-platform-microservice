package com.pl.platform.svc.driver.adapter.rest

import com.pl.platform.svc.driver.adapter.rest.request.CreateDriverRequest
import com.pl.platform.svc.driver.adapter.rest.response.DriverResponse
import com.pl.platform.svc.driver.application.handler.CreateDriverHandler
import com.pl.platform.svc.driver.application.handler.GetAllDriversHandler
import com.pl.platform.svc.driver.application.handler.GetDriverHandler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
    @Operation(
        summary = "Create driver",
        description = """
            Creates a new driver.
            
            The request must contain the driver's basic information,
            including first name, last name, phone number and email address.
            
            Returns the created driver with its generated unique identifier.
        """
    )
    fun create(
        @Valid @RequestBody request: CreateDriverRequest
    ): DriverResponse =
        createDriverHandler.handle(request.toCommand())

    @GetMapping
    @Operation(
        summary = "Get drivers",
        description = """
            Returns a list of drivers.
            
            An optional driver ID can be provided to filter the results
            to a specific driver.
            
            When no driver ID is provided, all available drivers are returned.
        """,
        parameters = [
            Parameter(
                name = "driverId",
                description = "Filters drivers by their unique identifier",
                required = false,
                `in` = ParameterIn.QUERY
            )
        ]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Drivers returned successfully"
            ),
            ApiResponse(
                responseCode = "429",
                description = "Too many requests. The client has exceeded the allowed request rate."
            )
        ]
    )
    fun getAll(
        @RequestParam(required = false) driverId: UUID?
    ): List<DriverResponse> =
        getAllDrivers.handle(driverId)

    @GetMapping("/{driverId}")
    @Operation(
        summary = "Get driver by ID",
        description = """
            Returns a driver identified by the given driver ID.
            
            If no driver exists with the specified ID, the response is empty.
        """,
        parameters = [
            Parameter(
                name = "driverId",
                description = "Unique identifier of the driver",
                required = true,
                `in` = ParameterIn.PATH
            )
        ]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Driver returned successfully"
            ),
            ApiResponse(
                responseCode = "429",
                description = "Too many requests. The client has exceeded the allowed request rate."
            )
        ]
    )
    fun getDriver(
        @PathVariable driverId: UUID
    ): DriverResponse? =
        getDriver.handle(driverId)
}