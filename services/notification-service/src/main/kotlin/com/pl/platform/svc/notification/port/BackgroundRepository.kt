package com.pl.platform.svc.notification.port

import io.smallrye.mutiny.Uni

interface BackgroundRepository {

    fun recordExecution(
        taskName: String,
        durationMs: Long,
        error: Throwable? = null
    ): Uni<Void>
}