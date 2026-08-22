package com.pl.platform.svc.delivery.application.consumer
import com.pl.platform.common.messaging.event.payments.PaymentPaidEvent
import com.pl.platform.svc.delivery.application.command.AssignDriverCommand
import com.pl.platform.svc.delivery.application.handler.AssignDriverHandler
import com.pl.platform.svc.delivery.domain.DeliveryId
import com.pl.platform.svc.messaging.adapter.publisher.KafkaEventPublisher
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class PaymentsServiceConsumer(
    private val jsonMapper: JsonMapper,
    private val assignDriverHandler: AssignDriverHandler
) {

    @KafkaListener(topics = ["payment.paid"], groupId = "delivery-service")
    fun process(message: ConsumerRecord<String, String>) {
        val eventType = String(message.headers().lastHeader(KafkaEventPublisher.HEADER_EVENT_TYPE).value(), Charsets.UTF_8);
        logger.info("Delivery payment accepted/paid: {}", eventType )

        // TODO: procesed events maybe
        val event =  jsonMapper.readValue(message.value(), PaymentPaidEvent::class.java)

        logger.info("Delivery id: {}", event.deliveryId )

        assignDriverHandler.handle(AssignDriverCommand(
            deliveryId = DeliveryId(event.deliveryId),
            driverId = null
        ))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PaymentsServiceConsumer::class.java)
    }

}