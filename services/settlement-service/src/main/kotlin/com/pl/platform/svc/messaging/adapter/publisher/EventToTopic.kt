package com.pl.platform.svc.messaging.adapter.publisher
import org.springframework.stereotype.Component

@Component
class EventToTopic {
    fun map(eventType: String, module: String): String {
        return when (eventType) {
            "driver.settlement.completed" -> "driver.settlement.completed"
            else -> "$module.events"
        }
    }
}