package com.pl.platform.svc.notification.application.command

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.domain.NotificationId
import com.pl.platform.svc.notification.domain.NotificationStatus
import com.pl.platform.svc.notification.port.NotificationRepository
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant
import java.util.*


data class CreateNotificationCommand(
    val eventId: UUID,
    val eventType: String,
    val recipient: String,
    val channel: NotificationChannel,
    val payload: String
)

@ApplicationScoped
class CreateNotification(
    private val repository: NotificationRepository,
    private val pool: Pool
) {

    fun execute(
        command: CreateNotificationCommand
    ): Uni<Notification> {

        val notification = Notification(
            id = NotificationId(UUID.randomUUID()),
            eventId = command.eventId,
            eventType = command.eventType,
            recipient = command.recipient,
            channel = command.channel,
            payload = command.payload,
            status = NotificationStatus.PENDING,
            attempts = 0,
            lastError = null,
            createdAt = Instant.now(),
            sentAt = null
        )

        return pool.withConnection { connection ->
                repository.create(
                    connection,
                    notification
                ).onItem().transform { current ->
                    requireNotNull(current) {
                        "Notification not created"
                    }
                }
            }
    }
}