package com.pl.platform.svc.messaging.adapter.publisher
import com.pl.platform.common.messaging.OutboxMessage
import com.pl.platform.common.messaging.port.EventPublisher
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Component
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@Component
@ConditionalOnProperty(
    prefix = "delivery",
    name = ["event-bus"],
    havingValue = "logger"
)
class LoggingEventPublisher : EventPublisher {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun publish(event: OutboxMessage) {
        log.info(
            "Publishing delivery event type={} eventId={} aggregateId={} payload={}",
            event.eventType,
            event.id.value,
            event.aggregateId,
            event.payload
        )
    }
}