package com.pl.platform.svc.notification.adapter.persistence
import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.domain.NotificationId
import com.pl.platform.svc.notification.port.NotificationRepository
import com.pl.platform.svc.notification.domain.NotificationStatus
import com.pl.platform.svc.notification.query.NotificationQuery
import com.pl.platform.svc.notification.query.NotificationQueryRepository
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import java.time.ZoneOffset
import java.util.UUID

@ApplicationScoped
class PgNotificationQueryRepository(private val pool: Pool
): NotificationQueryRepository {
    override fun find(query: NotificationQuery): Uni<List<Notification>> {
        val conditions = mutableListOf<String>()
        val parameters = Tuple.tuple()

        query.status?.let {
            conditions += "status = $${parameters.size() + 1}"
            parameters.addString(it.name)
        }

        query.eventId?.let {
            conditions += "event_id = $${parameters.size() + 1}"
            parameters.addUUID(it)
        }

        query.fromAtInstant().let {
            conditions += "created_at >= $${parameters.size() + 1}"
            parameters.addOffsetDateTime(
                it?.atOffset(ZoneOffset.UTC)
            )
        }

        query.toAtInstant()?.let {
            conditions += "created_at < $${parameters.size() + 1}"
            parameters.addOffsetDateTime(
                it.atOffset(ZoneOffset.UTC)
            )
        }

        val where =
            if (conditions.isEmpty()) {
                ""
            } else {
                "WHERE ${conditions.joinToString(" AND ")}"
            }

        val sql =
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
            $where
            ORDER BY created_at DESC
            """.trimIndent()

        return pool
            .preparedQuery(sql)
            .execute(parameters)
            .onItem()
            .transform { rows ->
                rows.map { MapperRow.map(it) }
            }
    }
}