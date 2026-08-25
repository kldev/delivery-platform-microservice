package com.pl.platform.svc.notification.service

import com.pl.platform.common.messaging.event.Event
import com.pl.platform.common.messaging.event.delivery.DeliveryEventType
import com.pl.platform.common.messaging.event.payments.PaymentEventType
import com.pl.platform.svc.notification.domain.NotificationChannel
import jakarta.enterprise.context.ApplicationScoped
import kotlin.collections.get

@ApplicationScoped
class NotificationPolicy {

    private val rules = mapOf(
        DeliveryEventType.COMPLETED.value to setOf(
            NotificationChannel.EMAIL
        ),

        PaymentEventType.Completed.value to setOf(
            NotificationChannel.EMAIL,
            NotificationChannel.PUSH
        ),

        PaymentEventType.Declined.value to setOf(
            NotificationChannel.EMAIL,
            NotificationChannel.SMS,
            NotificationChannel.PUSH
        )
    )

    fun channelsFor(event: Event): Set<NotificationChannel> =
        rules[event.eventType].orEmpty()
}