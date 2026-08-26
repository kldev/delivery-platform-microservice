package com.pl.platform.svc.idempotency.exception

class IdempotencyReplayException(    val status: Int,
                                     val responseBody: String) : RuntimeException() {
}