package com.pl.platform.svc.reconciliation.adapter.messaging

import com.pl.platform.common.messaging.event.payments.PaymentCompletedEvent
import com.pl.platform.svc.reconciliation.application.use_case.ProcessPaymentCompletedHandler
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
class PaymentCompletedKafkaListener(
    private val jsonMapper: JsonMapper,
    private val handler: ProcessPaymentCompletedHandler) {

    @KafkaListener(
        topics = ["payment.completed"],
        groupId = "reconciliation-service"
    )
    fun consume(message: ConsumerRecord<String, String>) {
        val event = jsonMapper.readValue(
            message.value(),
            PaymentCompletedEvent::class.java
        )

        handler.handle(event)
    }
}