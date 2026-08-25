package com.pl.platform.svc.notification.sender

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class EmailNotificationSender(
    override val channel: NotificationChannel  = NotificationChannel.EMAIL,
) : NotificationSender {

    override fun supports(channel: NotificationChannel): Boolean =
        channel == NotificationChannel.EMAIL

    override fun send(
        notification: Notification,
        recipient: NotificationRecipient,
    ): Uni<Void> {

        Log.info("Sending email notification ${recipient.email}")
        return  Uni.createFrom().voidItem()
    }

}