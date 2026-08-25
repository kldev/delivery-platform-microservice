package com.pl.platform.svc.messaging.adapter.persistence

import com.pl.platform.svc.messaging.port.ProcessedEventRepository
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class PgProcessedEventRepository(
    private val pool: Pool
) : ProcessedEventRepository {
    override fun exists(eventId: UUID): Uni<Boolean> =
        pool.preparedQuery(
            """
                   SELECT EXISTS (
                    SELECT 1
                    FROM processed_events
                    WHERE event_id = $1
                )
            """.trimIndent()
        ).execute(Tuple.of(eventId))
            .onItem()
            .transform { rows ->
                rows.first().getBoolean(0)
             }

    override fun save(eventId: UUID, eventType: String): Uni<Boolean> =
        pool
            .preparedQuery(
                """
                INSERT INTO processed_events (
                    event_id,
                    event_type,
                    processed_at
                )
                VALUES ($1, $2, CURRENT_TIMESTAMP)
                ON CONFLICT (event_id) DO NOTHING
                """.trimIndent()
            )
            .execute(Tuple.of(eventId, eventType))
            .onItem()
            .transform { result ->
                result.rowCount() == 1
            }
}