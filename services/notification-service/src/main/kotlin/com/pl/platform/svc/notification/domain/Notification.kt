package com.pl.platform.svc.notification.domain

import com.pl.platform.common.messaging.event.Event
import java.util.UUID
import java.time.Instant

data class Notification(
    val id: NotificationId,
    val eventId: UUID,
    val eventType: String,
    val recipient: String,
    val channel: NotificationChannel,
    val payload: String,
    val status: NotificationStatus,
    val attempts: Int,
    val lastError: String? = null,
    val createdAt: Instant,
    val sentAt: Instant? = null
)
{
    companion object {
        fun create(event: Event, channel: NotificationChannel, payload: String): Notification = Notification(
            id = NotificationId(UUID.randomUUID()),
            eventId = event.eventId,
            eventType = event.eventType,
            recipient = "",
            channel = channel,
            payload = payload,
            attempts = 0,
            createdAt = Instant.now(),
            status = NotificationStatus.PENDING
        )
    }
}