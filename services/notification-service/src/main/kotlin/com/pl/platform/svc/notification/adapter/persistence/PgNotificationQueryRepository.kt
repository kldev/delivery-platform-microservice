package com.pl.platform.svc.notification.adapter.persistence
import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.domain.NotificationId
import com.pl.platform.svc.notification.port.NotificationRepository
import com.pl.platform.svc.notification.domain.NotificationStatus
import com.pl.platform.svc.notification.query.NotificationQuery
import com.pl.platform.svc.notification.query.NotificationQueryRepository
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.ZoneOffset
import java.util.UUID

@ApplicationScoped
class PgNotificationQueryRepository(private val pool: Pool
): NotificationQueryRepository {

    @ConfigProperty(
        name = "notification.sql.log",
        defaultValue = "false"
    )
    var logSql: Boolean = false

    override fun find(query: NotificationQuery): Uni<SliceResponse<Notification>> {
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

        query.fromAtInstant()?.let {
            conditions += "created_at >= $${parameters.size() + 1}"
            parameters.addOffsetDateTime(
                it.atOffset(ZoneOffset.UTC)
            )
        }

        query.toAtInstant()?.let {
            conditions += "created_at < $${parameters.size() + 1}"
            parameters.addOffsetDateTime(
                it.atOffset(ZoneOffset.UTC)
            )
        }

        val limit = query.limit.coerceAtMost(500)
        parameters.addLong(limit.toLong() + 1)
        parameters.addLong(query.offset.toLong())

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
            ORDER BY created_at DESC, id DESC
            LIMIT $${parameters.size() -1}
            OFFSET $${parameters.size()}
            """.trimIndent()

        if (logSql) {
            Log.info("SQL: $sql")
        }

        return pool
            .preparedQuery(sql)
            .execute(parameters)
            .onItem()
            .transform { rows ->
                val items = rows.map(MapperRow::map)
                SliceResponse(
                    content = items.take(query.limit),
                    hasNext = items.size > query.limit
                )
            }
    }
}