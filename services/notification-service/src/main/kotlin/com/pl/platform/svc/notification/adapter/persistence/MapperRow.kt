package com.pl.platform.svc.notification.adapter.persistence

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.domain.NotificationId
import com.pl.platform.svc.notification.domain.NotificationStatus

object MapperRow {
    fun map(row: io.vertx.mutiny.sqlclient.Row): Notification =
        Notification(
            id = NotificationId(row.getUUID("id")),
            eventId = row.getUUID("event_id"),
            eventType = row.getString("event_type"),
            recipient = row.getString("recipient"),
            channel = NotificationChannel.valueOf(
                row.getString("channel")
            ),
            payload = row.getValue("payload").toString(),
            status = NotificationStatus.valueOf(
                row.getString("status")
            ),
            attempts = row.getInteger("attempts"),
            lastError = row.getString("last_error"),
            createdAt = row.getOffsetDateTime("created_at").toInstant(),
            sentAt = row.getOffsetDateTime("sent_at")?.toInstant()
        )
}