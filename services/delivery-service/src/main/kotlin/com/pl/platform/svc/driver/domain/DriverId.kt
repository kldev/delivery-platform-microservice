package com.pl.platform.svc.driver.domain

import java.util.UUID

@JvmInline
value class DriverId(
    val value: UUID
) {
    companion object {
        fun generate(): DriverId =
            DriverId(UUID.randomUUID())
    }
}