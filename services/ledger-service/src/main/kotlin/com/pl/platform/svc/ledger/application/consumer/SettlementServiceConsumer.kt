package com.pl.platform.svc.ledger.application.consumer
import com.pl.platform.svc.integration.event.DriverSettlementCompletedEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

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
            "Received driver settlement completed event {} for settlement {}",
            event.eventId,
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