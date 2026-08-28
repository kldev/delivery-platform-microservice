package com.pl.platform.svc.delivery.adapter.rest.response

import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import java.math.BigDecimal
import java.util.UUID

data class DeliveryCreateResponse(
    val id: UUID,
    val price: BigDecimal,
    val status: DeliveryStatus
)