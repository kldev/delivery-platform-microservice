package com.pl.platform.svc.reconciliation.adapter.messaging

import com.pl.platform.svc.integration.event.PaymentCompletedEvent
import com.pl.platform.svc.integration.event.SettlementCreatedEvent
import com.pl.platform.svc.reconciliation.application.use_case.ProcessSettlementCreatedHandler
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
@ConditionalOnProperty(
    prefix = "delivery",
    name = ["event-bus"],
    havingValue = "kafka"
)
class SettlementCreatedKafkaListener(private val jsonMapper: JsonMapper,
                                     private val handler: ProcessSettlementCreatedHandler) {

    @KafkaListener(
        topics = ["settlement.created"],
        groupId = "reconciliation-service"
    )
    fun consume(message: ConsumerRecord<String, String>) {
        val event = jsonMapper.readValue(
            message.value(),
            SettlementCreatedEvent::class.java
        )

        handler.handle(event)
    }
}