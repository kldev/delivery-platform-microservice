package com.pl.platform.svc.delivery.adapter.rest

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryItemResponse
import com.pl.platform.svc.delivery.application.query.DeliveryQueryRepository
import com.pl.platform.svc.delivery.application.query.DeliveryStatusDurationItem
import com.pl.platform.svc.delivery.application.query.DeliveryStatusDurationQueryRepository
import com.pl.platform.svc.delivery.application.query.GetDeliveryQuery
import com.pl.platform.svc.delivery.application.query.GetDeliveryStatusDurationQuery
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Deliveries")
class DeliveryQueryController(
    private val deliveryQueryRepository: DeliveryQueryRepository,
    private val deliveryStatusDurationQueryRepository: DeliveryStatusDurationQueryRepository) {

    @GetMapping
    @Operation(
        summary = "Get deliveries",
        description = """
        Returns a paginated list of deliveries.
        
        Results are sorted by creation time in descending order, with the
        newest deliveries returned first.
        
        Results are returned as a slice, so the response contains information
        about whether more results are available without performing a total
        count query.
        
        Optional filters can be used to search by delivery status or delivery ID.
    """,
        parameters = [
            Parameter(
                name = "status",
                description = "Filters deliveries by status",
                required = false,
                `in` = ParameterIn.QUERY
            ),
            Parameter(
                name = "deliveryId",
                description = "Filters by the unique identifier of a delivery",
                required = false,
                `in` = ParameterIn.QUERY
            ),
            Parameter(
                name = "size",
                description = "Number of deliveries to return. Maximum value is 500.",
                required = false,
                `in` = ParameterIn.QUERY
            ),
            Parameter(
                name = "page",
                description = "Zero-based page number",
                required = false,
                `in` = ParameterIn.QUERY
            )
        ]
    )
    fun getAll(@RequestParam(required = false) status: DeliveryStatus?,
               @RequestParam(required = false) deliveryId: UUID?,
               @RequestParam(required = false) driverId: UUID?,
               @RequestParam(required = false, defaultValue = "100")@Max(500) size: Int,
               @RequestParam(required = false, defaultValue = "0") page: Int)
        = deliveryQueryRepository.search(
            GetDeliveryQuery(status, deliveryId, driverId), PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.DESC, "createdAt")
            )
        );


    @GetMapping("status-duration")
    @Operation(
        summary = "Get delivery status durations",
        description = """
        Returns the status history of deliveries together with the duration
        of each status.
        
        An optional delivery ID can be provided to return status durations
        for a specific delivery.
        
        Results are sorted by the time when the status started, with the
        oldest status entries returned first.
        
        Results are returned as a slice, so the response contains information
        about whether more results are available without performing a total
        count query.
    """,
        parameters = [
            Parameter(
                name = "deliveryId",
                description = "Filters status durations for a specific delivery",
                required = false,
                `in` = ParameterIn.QUERY
            ),
            Parameter(
                name = "size",
                description = "Number of status duration entries to return. Maximum value is 500.",
                required = false,
                `in` = ParameterIn.QUERY
            ),
            Parameter(
                name = "page",
                description = "Zero-based page number",
                required = false,
                `in` = ParameterIn.QUERY
            )
        ]
    )
    fun getStatusDuration(
        @RequestParam(required = false) deliveryId: UUID?,
        @RequestParam(required = false, defaultValue = "100")@Max(500) size: Int,
        @RequestParam(required = false, defaultValue = "0") page: Int)

        = deliveryStatusDurationQueryRepository.search(
            GetDeliveryStatusDurationQuery(deliveryId), PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.ASC, "statusFrom")
            )
        );

}