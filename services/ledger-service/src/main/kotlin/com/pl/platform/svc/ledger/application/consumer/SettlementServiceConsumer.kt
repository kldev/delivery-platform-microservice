package com.pl.platform.svc.ledger.application.consumer
import com.pl.platform.common.messaging.event.settlement.DriverSettlementCompletedEvent
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
class SettlementServiceConsumer(
    private val jsonMapper: JsonMapper,
    private val processor: DriverSettlementCompletedProcessor,
) {
    @KafkaListener(
        topics = ["driver.settlement.completed"],
        groupId = "ledger-service",
    )
    fun processDriverSettlementCompleted(
        message: ConsumerRecord<String, String>,
    ) {
        val event =
            jsonMapper.readValue(
                message.value(),
                DriverSettlementCompletedEvent::class.java,
            )

        logger.info(
            "Received driver settlement completed delivery id {} for settlement {}",
            event.deliveryId,
            event.settlementId,
        )

        processor.process(event)
    }

    companion object {
        private val logger =
            LoggerFactory.getLogger(
                SettlementServiceConsumer::class.java,
            )
    }

}