package com.pl.platform.svc.notification.fixture

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.domain.NotificationId
import com.pl.platform.svc.notification.port.NotificationRepository
import com.pl.platform.svc.notification.domain.NotificationStatus
import io.vertx.mutiny.sqlclient.Pool
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class NotificationFixture(
    private val repository: NotificationRepository,
    private val pool: Pool
) {

    fun create(
        id: UUID = UUID.randomUUID(),
        eventId: UUID = UUID.randomUUID(),
        eventType: String = "DeliveryCreated",
        recipient: String = "test@example.com",
        channel: NotificationChannel = NotificationChannel.EMAIL,
        payload: String = """{"deliveryId":"${UUID.randomUUID()}"}""",
        status: NotificationStatus = NotificationStatus.PENDING,
        attempts: Int = 0,
        lastError: String? = null,
        createdAt: Instant = Instant.now(),
        sentAt: Instant? = null
    ): Notification {

        val notification = Notification(
            id = NotificationId(id),
            eventId = eventId,
            eventType = eventType,
            recipient = recipient,
            channel = channel,
            payload = payload,
            status = status,
            attempts = attempts,
            lastError = lastError,
            createdAt = createdAt,
            sentAt = sentAt
        )

        return pool.withConnection { connection ->
            repository
                .create(connection, notification)
                .onItem()
                .transform { requireNotNull(it) }
        }
            .await()
            .indefinitely()
    }
}