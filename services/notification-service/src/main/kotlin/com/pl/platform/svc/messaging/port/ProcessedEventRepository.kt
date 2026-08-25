package com.pl.platform.svc.messaging.port

import com.pl.platform.common.messaging.event.Event
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.SqlConnection
import java.util.UUID

interface ProcessedEventRepository {

    fun exists(eventId: UUID): Uni<Boolean>

    fun save(
        connection: SqlConnection,
        event: Event
    ): Uni<Boolean>
}