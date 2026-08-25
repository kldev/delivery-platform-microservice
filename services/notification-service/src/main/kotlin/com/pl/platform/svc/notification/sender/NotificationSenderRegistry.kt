package com.pl.platform.svc.notification.sender

import com.pl.platform.svc.notification.domain.NotificationChannel

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Any
import jakarta.enterprise.inject.Instance

@ApplicationScoped
class NotificationSenderRegistry(
    @Any senders: Instance<NotificationSender>,
) {

    private val sendersByChannel =
        senders.associateBy { it.channel}

    fun get(channel: NotificationChannel): NotificationSender? =
        sendersByChannel[channel]
}