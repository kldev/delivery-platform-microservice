package com.pl.platform.svc.notification.adapter.rest.model

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.domain.NotificationStatus
import java.time.Instant
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val eventId: UUID,
    val eventType: String,
    val recipient: String,
    val channel: NotificationChannel,
    val payload: String,
    val status: NotificationStatus,
    val attempts: Int,
    val lastError: String?,
    val createdAt: Instant,
    val sentAt: Instant?
) {

    companion object {

        fun from(notification: Notification) =
            NotificationResponse(
                id = notification.id.value,
                eventId = notification.eventId,
                eventType = notification.eventType,
                recipient = notification.recipient,
                channel = notification.channel,
                payload = notification.payload,
                status = notification.status,
                attempts = notification.attempts,
                lastError = notification.lastError,
                createdAt = notification.createdAt,
                sentAt = notification.sentAt
            )
    }
}