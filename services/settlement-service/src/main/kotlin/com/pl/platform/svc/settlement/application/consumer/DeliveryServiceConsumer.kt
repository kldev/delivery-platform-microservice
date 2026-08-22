package com.pl.platform.svc.settlement.application.consumer
import com.pl.platform.common.messaging.event.payments.PaymentDeclinedEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class DeliveryServiceConsumer(
    private val jsonMapper: JsonMapper,
    private val processor: DeliveryCompletedProcessor
) {

    @KafkaListener(
        topics = ["delivery.completed"],
        groupId = "settlement-service",
    )
    fun processDeliveryComplete(
        message: ConsumerRecord<String, String>,
    ) {
        val event =
            jsonMapper.readValue(
                message.value(),
                DeliveryCompletedEvent::class.java,
            )

        logger.info(
            "Received delivery completed event {} for delivery {}",
            event.eventId,
            event.deliveryId,
        )

        processor.process(event)

    }

    companion object {
        private val logger =
            LoggerFactory.getLogger(
                DeliveryServiceConsumer::class.java,
            )
    }
}