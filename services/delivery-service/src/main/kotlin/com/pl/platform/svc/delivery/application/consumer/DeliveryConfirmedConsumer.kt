package com.pl.platform.svc.delivery.application.consumer
import com.pl.platform.svc.delivery.application.event.DeliveryConfirmedEvent
import com.pl.platform.svc.messaging.adapter.publisher.KafkaEventPublisher
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper

@Component
class DeliveryConfirmedConsumer(
    private val jsonMapper: JsonMapper,
) {

    @KafkaListener(topics = ["delivery.confirmed"], groupId = "delivery-service")
    fun process(message: ConsumerRecord<String, String>) {
        val eventType = message.headers().lastHeader(KafkaEventPublisher.HEADER_EVENT_TYPE);
        logger.info("Delivery confirmed: {}", eventType )

        val event =  jsonMapper.readValue(message.value(), DeliveryConfirmedEvent::class.java)

        logger.info("Delivery id: {}", event.deliveryId )

    }

    companion object {
        private val logger = LoggerFactory.getLogger(DeliveryConfirmedConsumer::class.java)
    }

}