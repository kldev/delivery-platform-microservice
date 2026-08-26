package com.pl.platform.svc.idempotency.domain

enum class IdempotencyResult {
    NEW,
    REPLAY,
    CONFLICT
}