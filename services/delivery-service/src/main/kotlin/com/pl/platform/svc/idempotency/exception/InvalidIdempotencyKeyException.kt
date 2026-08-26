package com.pl.platform.svc.idempotency.exception

class InvalidIdempotencyKeyException :
    RuntimeException("X-Idempotency-Key must be a valid UUID")