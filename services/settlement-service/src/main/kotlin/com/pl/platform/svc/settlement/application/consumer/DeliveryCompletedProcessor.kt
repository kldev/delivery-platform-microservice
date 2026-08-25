package com.pl.platform.svc.settlement.application.consumer
import com.pl.platform.common.messaging.event.delivery.DeliveryCompletedEvent
import com.pl.platform.svc.messaging.adapter.persistence.ProcessedEventJpaEntity
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataProcessedEventRepository
import com.pl.platform.svc.settlement.application.create_settlement.CreateSettlementCommand
import com.pl.platform.svc.settlement.application.create_settlement.CreateSettlementHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
class DeliveryCompletedProcessor(
    private val handler: CreateSettlementHandler,
    private val processedEventRepository: SpringDataProcessedEventRepository
) {

    @Transactional
    fun process(event: DeliveryCompletedEvent) {

        if (processedEventRepository.existsByEventId(event.eventId)) {
            logger.info(
                "Event {} already processed, skipping",
                event.eventId
            )
            return
        }

        handler.handle(
            CreateSettlementCommand(
              deliveryId = event.deliveryId,
                driverId =  event.driverId,
                driverFullName = event.driverFullName,
                distanceKm = event.distanceKm,
                deliveryAmount = event.price,
                currency =event.currency,
                completedAt = event.occurredAt
            )
        )

        processedEventRepository.save(
            ProcessedEventJpaEntity.create(
                eventId = event.eventId,
                eventType = event.eventType
            )
        )
    }

    companion object {
        private val logger =
            LoggerFactory.getLogger(
                DeliveryCompletedProcessor::class.java
            )
    }
}
