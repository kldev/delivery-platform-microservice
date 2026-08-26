package com.pl.platform.svc.idempotency.exception

class MissingIdempotencyKeyException :
    RuntimeException("X-Idempotency-Key header is required")