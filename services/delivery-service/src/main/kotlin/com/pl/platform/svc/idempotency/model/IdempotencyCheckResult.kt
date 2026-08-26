package com.pl.platform.svc.idempotency.model

sealed interface IdempotencyCheckResult {

    data class New(
        val requestHash: String
    ) : IdempotencyCheckResult

    data class Replay(
        val status: Int,
        val body: String
    ) : IdempotencyCheckResult
}