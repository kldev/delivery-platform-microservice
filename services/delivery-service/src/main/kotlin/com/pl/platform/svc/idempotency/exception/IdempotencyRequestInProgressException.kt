package com.pl.platform.svc.idempotency.exception

class IdempotencyRequestInProgressException : Exception("Request with this Idempotency-Key is already in progress") {
}