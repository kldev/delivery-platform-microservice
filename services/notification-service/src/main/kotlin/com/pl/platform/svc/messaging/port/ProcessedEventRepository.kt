package com.pl.platform.svc.messaging.port

import io.smallrye.mutiny.Uni
import java.util.UUID

interface ProcessedEventRepository {

    fun exists(eventId: UUID): Uni<Boolean>

    fun save(
        eventId: UUID,
        eventType: String
    ): Uni<Boolean>
}