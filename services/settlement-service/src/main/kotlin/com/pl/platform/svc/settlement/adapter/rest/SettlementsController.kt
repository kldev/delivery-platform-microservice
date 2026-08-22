package com.pl.platform.svc.settlement.adapter.rest

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.settlement.application.query.GetSettlementsListHandler
import com.pl.platform.svc.settlement.application.query.GetSettlementsListQuery
import com.pl.platform.svc.settlement.domain.Settlement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@Validated
@RequestMapping("/api/settlements")
@Tag(name = "Settlements", description = "Settlement service")
@RestController
class SettlementsController(private val handler: GetSettlementsListHandler) {

    @GetMapping
    fun search(@RequestParam(required = false) search: String?,
               @RequestParam(required = false, defaultValue = "100")@Max(500) size: Int,
               @RequestParam(required = false, defaultValue = "0") page: Int,
               @RequestParam(required = false) driverId: UUID?,
               @RequestParam(required = false) deliveryId: UUID?): SliceResponse<Settlement>
    {
        val query = GetSettlementsListQuery(
            search=search,
            size=size, page=page,
            driverId=driverId,
            deliveryId=deliveryId )

        return handler.handle(query);
    }
}