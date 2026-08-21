package com.pl.platform.svc.messaging.adapter.publisher

import com.pl.platform.common.messaging.OutboxMessage
import com.pl.platform.common.messaging.port.EventPublisher
import com.pl.platform.svc.delivery.application.event.DeliveryEventType
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "delivery",
    name = ["event-bus"],
    havingValue = "kafka"
)
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventToTopic: EventToTopic
) : EventPublisher {

    companion object {
        const val HEADER_EVENT_ID = "event-id"
        const val HEADER_EVENT_TYPE = "event-type"
        const val HEADER_EVENT_VERSION = "event-version"
        const val HEADER_OCCURRED_AT = "occurred-at"
    }

    override fun publish(event: OutboxMessage) {

        val record = ProducerRecord(
            eventToTopic.map(event.eventType, event.module),
            event.aggregateId.toString(),
            event.payload
        )

        record.headers().add(
            HEADER_EVENT_ID,
            event.id.value.toString().toByteArray()
        )

        record.headers().add(
            HEADER_EVENT_TYPE,
            event.eventType.toByteArray()
        )

        record.headers().add(
            HEADER_EVENT_VERSION,
            "1".toByteArray()
        )

        record.headers().add(
            HEADER_OCCURRED_AT,
            event.occurredAt.toString().toByteArray()
        )

        kafkaTemplate.send(record).get()
    }

}