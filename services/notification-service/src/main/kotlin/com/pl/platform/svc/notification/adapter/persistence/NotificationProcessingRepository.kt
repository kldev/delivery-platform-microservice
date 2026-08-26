package com.pl.platform.svc.notification.adapter.persistence

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationStatus
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import java.time.OffsetDateTime
import java.util.UUID

@ApplicationScoped
class NotificationProcessingRepository(
    private val pool: Pool,
) {

    fun claimBatch(
        status: NotificationStatus,
        batchSize: Int,
    ): Uni<List<Notification>> =
        pool.withTransaction { connection ->
            connection
                .preparedQuery(
                    """
                    UPDATE notifications
                    SET
                        status = 'PROCESSING',
                        attempts = attempts + 1
                    WHERE id IN (
                        SELECT id
                        FROM notifications
                        WHERE status = $1
                        ORDER BY created_at
                        LIMIT $2
                        FOR UPDATE SKIP LOCKED
                    )
                    RETURNING
                        id,
                        event_id,
                        event_type,
                        recipient,
                        channel,
                        payload,
                        status,
                        attempts,
                        last_error,
                        created_at,
                        sent_at
                    """.trimIndent()
                )
                .execute(
                    Tuple.of(status.name, batchSize)
                )
                .map { rows ->
                    rows.map { row ->
                        MapperRow.map(row)
                    }
                }
        }

    fun updateRecipient(id: UUID, recipientJson: String): Uni<Void> =
        pool.preparedQuery(
            """
            UPDATE notifications
                set recipient = $2,              
            where id = $1
        """.trimIndent()
        ).execute(
            Tuple.tuple().addUUID(id)
                .addString(recipientJson)
        ).replaceWithVoid()


    fun markSent(id: UUID): Uni<Void> =
        pool.preparedQuery(
            """
        UPDATE notifications
        SET status = 'SENT',
            sent_at = $2
        WHERE id = $1
        """.trimIndent()
        )
            .execute(Tuple.tuple().addUUID(id).addOffsetDateTime(OffsetDateTime.now()))
            .replaceWithVoid()

    fun markFailed(
        id: UUID,
        error: String,
    ): Uni<Void> =
        pool.preparedQuery(
            """
        UPDATE notifications
        SET status = 'FAILED',
            attempts = attempts + 1,
            last_error = $2
        WHERE id = $1
        """.trimIndent()
        )
            .execute(
                Tuple.of(id, error)
            )
            .replaceWithVoid()
}
