package com.pl.platform.svc.idempotency.domain

import java.time.Instant
import java.util.UUID

data class IdempotencyRecord(
    val id: UUID,
    val key: UUID,
    val requestHash: String,
    val responseStatus: Int,
    val responseBody: String,
    val createdAt: Instant,
    val expiresAt: Instant?
)