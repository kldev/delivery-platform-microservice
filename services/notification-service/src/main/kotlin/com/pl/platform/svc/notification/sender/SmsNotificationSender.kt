package com.pl.platform.svc.notification.sender

import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SmsNotificationSender(
    override val channel: NotificationChannel  = NotificationChannel.SMS,
) : NotificationSender {

    override fun supports(channel: NotificationChannel): Boolean =
        channel == NotificationChannel.SMS

    override fun send(
        notification: Notification,
        recipient: NotificationRecipient,
    ): Uni<Void> {
        Log.info("SmsNotificationSender sending notification ${recipient.phoneNumber}")
        return Uni.createFrom().voidItem()
    }

}