package com.pl.platform.svc.ledger.application.consumer

import com.pl.platform.svc.ledger.application.use_case.PostDriverSettlementCommand
import com.pl.platform.svc.ledger.application.use_case.PostDriverSettlementHandler
import com.pl.platform.svc.integration.event.DriverSettlementCompletedEvent
import com.pl.platform.svc.messaging.adapter.persistence.ProcessedEventJpaEntity
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataProcessedEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DriverSettlementCompletedProcessor(
    private val handler: PostDriverSettlementHandler,
    private val processedEventRepository: SpringDataProcessedEventRepository,
) {

    @Transactional
    fun process(event: DriverSettlementCompletedEvent) {

        if (processedEventRepository.existsByEventId(event.eventId)) {
            logger.info(
                "Event {} already processed, skipping",
                event.eventId,
            )
            return
        }

        handler.handle(
            PostDriverSettlementCommand(
                settlementId = event.settlementId,
                driverId = event.driverId,
                currency = event.currency,
                amount = event.amount,
                occurredAt = event.occurredAt,
                driverFullName = event.driverFullName,
            )
        )

        processedEventRepository.save(
            ProcessedEventJpaEntity.create(
                eventId = event.eventId,
                eventType = event.eventType,
            )
        )

        logger.info(
            "Event {} processed",
            event.eventId,
        )
    }

    companion object {
        private val logger =
            LoggerFactory.getLogger(
                DriverSettlementCompletedProcessor::class.java,
            )
    }
}