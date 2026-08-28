package com.pl.platform.svc.notification.sender

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class PushNotificationSender(
    override val channel: NotificationChannel  = NotificationChannel.PUSH,
) : NotificationSender {

    override fun supports(channel: NotificationChannel): Boolean =
        channel == NotificationChannel.PUSH

    override fun send(
        notification: Notification,
        recipient: NotificationRecipient,
    ): Uni<Void> {
        Log.info("Sending push notification ${recipient.fullName}")
        return Uni.createFrom().voidItem()
    }
}