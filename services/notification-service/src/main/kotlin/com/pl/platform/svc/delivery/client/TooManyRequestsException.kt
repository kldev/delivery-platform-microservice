package com.pl.platform.svc.delivery.client


class TooManyRequestsException(
    val retryAfterSeconds: Long?
) : RuntimeException("Remote API returned HTTP 429")