package com.pl.platform.svc.notification.port

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationId
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.SqlConnection
import java.util.UUID

interface NotificationRepository {

    fun create(connection: SqlConnection, notification: Notification): Uni<Notification?>

    fun update(notification: Notification): Uni<Notification>

    fun findById(id: NotificationId): Uni<Notification?>

    fun findByEventId(eventId: UUID): Uni<Notification?>

    fun delete(id: NotificationId): Uni<Boolean>
}