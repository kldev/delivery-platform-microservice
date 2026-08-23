package com.pl.platform.svc.delivery.application.consumer

import com.pl.platform.common.messaging.event.payments.PaymentDeclinedEvent
import com.pl.platform.common.messaging.event.payments.PaymentPaidEvent

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@ConditionalOnProperty(
    prefix = "delivery",
    name = ["event-bus"],
    havingValue = "kafka"
)
@Component
class PaymentsServiceConsumer(
    private val jsonMapper: JsonMapper,
    private val paymentPaidProcessor: PaymentPaidProcessor,
    private val paymentDeclinedProcessor: PaymentDeclinedProcessor,
) {

    @KafkaListener(
        topics = ["payment.paid"],
        groupId = "delivery-service",
    )
    fun processPaid(
        message: ConsumerRecord<String, String>,
    ) {
        val event =
            jsonMapper.readValue(
                message.value(),
                PaymentPaidEvent::class.java,
            )

        logger.info(
            "Received payment paid event {} for delivery {}",
            event.eventId,
            event.deliveryId,
        )

        paymentPaidProcessor.process(event)
    }

    @KafkaListener(
        topics = ["payment.declined"],
        groupId = "delivery-service",
    )
    fun processDeclined(
        message: ConsumerRecord<String, String>,
    ) {
        val event =
            jsonMapper.readValue(
                message.value(),
                PaymentDeclinedEvent::class.java,
            )

        logger.info(
            "Received payment declined event {} for delivery {}",
            event.eventId,
            event.deliveryId,
        )

        paymentDeclinedProcessor.process(event)
    }

    companion object {
        private val logger =
            LoggerFactory.getLogger(
                PaymentsServiceConsumer::class.java,
            )
    }
}