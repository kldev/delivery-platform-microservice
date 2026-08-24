package com.pl.platform.svc.delivery.adapter.rest

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryItemResponse
import com.pl.platform.svc.delivery.application.query.DeliveryQueryRepository
import com.pl.platform.svc.delivery.application.query.DeliveryStatusDurationItem
import com.pl.platform.svc.delivery.application.query.DeliveryStatusDurationQueryRepository
import com.pl.platform.svc.delivery.application.query.GetDeliveryQuery
import com.pl.platform.svc.delivery.application.query.GetDeliveryStatusDurationQuery
import com.pl.platform.svc.delivery.domain.DeliveryStatus
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
    fun getAll(@RequestParam(required = false) status: DeliveryStatus?,
               @RequestParam(required = false) deliveryId: UUID?,
               @RequestParam(required = false, defaultValue = "100")@Max(500) size: Int,
               @RequestParam(required = false, defaultValue = "0") page: Int): SliceResponse<DeliveryItemResponse> {

        return deliveryQueryRepository.search(
            GetDeliveryQuery(status, deliveryId), PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.DESC, "createdAt")
            )
        );
    }

    @GetMapping("status-duration")
    fun getStatusDuration(
        @RequestParam(required = false) deliveryId: UUID?,
        @RequestParam(required = false, defaultValue = "100")@Max(500) size: Int,
        @RequestParam(required = false, defaultValue = "0") page: Int,): SliceResponse<DeliveryStatusDurationItem> {

        return deliveryStatusDurationQueryRepository.search(
            GetDeliveryStatusDurationQuery(deliveryId), PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.ASC, "statusFrom")
            )
        );
    }
}