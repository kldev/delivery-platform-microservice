package com.pl.platform.svc.messaging.adapter.publisher
import com.pl.platform.svc.delivery.application.event.DeliveryEventType
import org.springframework.stereotype.Component

@Component
class EventToTopic {
    fun map(eventType: String, module: String): String {
        return when (eventType) {
            DeliveryEventType.CONFIRMED.value -> DeliveryEventType.CONFIRMED.value
            DeliveryEventType.COMPLETED.value -> DeliveryEventType.COMPLETED.value
            else -> "$module.events"
        }
    }
}