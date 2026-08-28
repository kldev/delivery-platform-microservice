package com.pl.platform.svc.idempotency.adapter

class IdempotencyKeyReuseException : RuntimeException("Idempotency-Key was already used with a different request body")