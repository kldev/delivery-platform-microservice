package com.pl.platform.svc.notification.domain

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
    val lastError: String?,
    val createdAt: Instant,
    val sentAt: Instant?
)