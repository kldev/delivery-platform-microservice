package com.pl.platform.svc.notification.sender

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import io.smallrye.mutiny.Uni

interface NotificationSender {

    val channel: NotificationChannel

    fun supports(channel: NotificationChannel): Boolean

    fun send(
        notification: Notification,
        recipient: NotificationRecipient,
    ): Uni<Void>
}