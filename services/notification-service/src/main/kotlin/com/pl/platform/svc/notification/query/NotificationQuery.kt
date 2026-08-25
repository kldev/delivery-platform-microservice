package com.pl.platform.svc.notification.query

import com.pl.platform.svc.notification.domain.NotificationStatus
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

data class NotificationQuery(
    val status: NotificationStatus? = null,
    val eventId: UUID? = null,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
    val timezone: ZoneId = ZoneId.of("Europe/Warsaw"),
    val offset: Int = 0,
    val limit: Int = 100
) {
    fun fromAtInstant(): Instant? {
        return from?.atStartOfDay(timezone)?.toInstant()
    }

    fun toAtInstant(): Instant? {
        return to?.plusDays(1)?.atStartOfDay(timezone)?.toInstant()
    }
}