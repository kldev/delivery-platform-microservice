package com.pl.platform.svc.ledger.application.query

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

data class GetLedgerEntryListQuery(
    val accountId: UUID? = null,
    val timezone: ZoneId = ZoneId.of("Europe/Warsaw"),
    val from: LocalDate? = null,
    val to: LocalDate? = null,
){
    fun fromAtInstant(): Instant? {
        return from?.atStartOfDay(timezone)?.toInstant()
    }

    fun toAtInstant(): Instant? {
        return to?.plusDays(1)?.atStartOfDay(timezone)?.toInstant()
    }
}
