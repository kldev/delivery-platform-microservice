package com.pl.platform.svc.settlement.application.query

import java.util.UUID

data class GetSettlementsListQuery(
    val search: String? = null,
    val driverId: UUID? = null,
    val deliveryId: UUID? = null,
    val page: Int = 0,
    val size: Int = 20,
)