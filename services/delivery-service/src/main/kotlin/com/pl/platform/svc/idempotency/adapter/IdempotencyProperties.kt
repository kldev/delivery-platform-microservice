package com.pl.platform.svc.idempotency.adapter

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("idempotency")
data class IdempotencyProperties(
    val timeout: Duration = Duration.ofMinutes(5)
)