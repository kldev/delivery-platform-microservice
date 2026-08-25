package com.pl.platform.svc.notification.adapter.persistence

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.domain.NotificationId
import com.pl.platform.svc.notification.port.NotificationRepository
import com.pl.platform.svc.notification.domain.NotificationStatus
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import java.time.ZoneOffset
import java.util.UUID

@ApplicationScoped
class PgNotificationRepository(
    private val pool: Pool
) : NotificationRepository {

    override fun create(
        notification: Notification
    ): Uni<Notification> =
        pool.preparedQuery(
            """
            INSERT INTO notifications (
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
            )
            VALUES (
                $1, $2, $3, $4, $5,
                $6::jsonb, $7, $8, $9, $10, $11
            )
            """.trimIndent()
        )
            .execute(
                Tuple.tuple()
                    .addUUID(notification.id.value)
                    .addUUID(notification.eventId)
                    .addString(notification.eventType)
                    .addString(notification.recipient)
                    .addString(notification.channel.name)
                    .addString(notification.payload)
                    .addString(notification.status.name)
                    .addInteger(notification.attempts)
                    .addString(notification.lastError)
                    .addOffsetDateTime(notification.createdAt.atOffset(ZoneOffset.UTC))
                    .addOffsetDateTime(notification.sentAt?.atOffset(ZoneOffset.UTC))
            )
            .replaceWith(notification)

    override fun update(
        notification: Notification
    ): Uni<Notification> =
        pool.preparedQuery(
            """
            UPDATE notifications
            SET
                status = $1,
                attempts = $2,
                last_error = $3,
                sent_at = $4
            WHERE id = $5
            """.trimIndent()
        )
            .execute(
                Tuple.tuple()
                    .addString(notification.status.name)
                    .addInteger(notification.attempts)
                    .addString(notification.lastError)
                    .addOffsetDateTime(notification.sentAt?.atOffset(ZoneOffset.UTC))
                    .addUUID(notification.id.value)
            )
            .replaceWith(notification)

    override fun findById(
        id: NotificationId
    ): Uni<Notification?> =
        pool.preparedQuery(
            """
            SELECT
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
            FROM notifications
            WHERE id = $1
            """.trimIndent()
        )
            .execute(Tuple.of(id.value))
            .onItem()
            .transform { rows ->
                rows.firstOrNull()?.let(MapperRow::map)
            }

    override fun findByEventId(
        eventId: UUID
    ): Uni<Notification?> =
        pool.preparedQuery(
            """
            SELECT
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
            FROM notifications
            WHERE event_id = $1
            """.trimIndent()
        )
            .execute(Tuple.of(eventId))
            .onItem()
            .transform { rows ->
                rows.firstOrNull()?.let(MapperRow::map)
            }

    override fun delete(
        id: NotificationId
    ): Uni<Boolean> =
        pool.preparedQuery(
            """
            DELETE FROM notifications
            WHERE id = $1
            """.trimIndent()
        )
            .execute(Tuple.of(id.value))
            .onItem()
            .transform { it.rowCount() == 1 }


}