package com.pl.platform.svc.notification.application.command

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationId
import com.pl.platform.svc.notification.domain.NotificationStatus
import com.pl.platform.svc.notification.port.NotificationRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import java.time.Instant

data class UpdateNotificationCommand(
    val id: NotificationId,
    val status: NotificationStatus,
    val attempts: Int,
    val lastError: String? = null,
    val sentAt: Instant? = null
)

@ApplicationScoped
class UpdateNotification(
    private val repository: NotificationRepository
) {

    fun execute(command: UpdateNotificationCommand): Uni<Notification> =
        repository.findById(command.id)
            .onItem()
            .ifNull()
            .failWith {
                IllegalStateException(
                    "Notification ${command.id.value} not found"
                )
            }
            .onItem()
            .transform { current ->
                requireNotNull(current) {
                    "Notification ${command.id.value} not found"
                }

                current.copy(
                    status = command.status,
                    attempts = command.attempts,
                    lastError = command.lastError,
                    sentAt = command.sentAt
                )
            }
            .onItem()
            .transformToUni(repository::update)
}