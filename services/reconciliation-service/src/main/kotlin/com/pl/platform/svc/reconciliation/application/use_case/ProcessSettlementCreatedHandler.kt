package com.pl.platform.svc.reconciliation.application.use_case

import com.pl.platform.common.messaging.event.settlement.SettlementCreatedEvent
import com.pl.platform.svc.messaging.adapter.persistence.ProcessedEventJpaEntity
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataProcessedEventRepository
import com.pl.platform.svc.reconciliation.domain.Reconciliation
import com.pl.platform.svc.reconciliation.port.ReconciliationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProcessSettlementCreatedHandler(
    private val reconciliationRepository: ReconciliationRepository,
    private val processedEventRepository: SpringDataProcessedEventRepository,
) {

    @Transactional
    fun handle(event: SettlementCreatedEvent) {

        if (processedEventRepository.existsByEventId(event.eventId)) {
            logger.info(
                "Event {} already processed, skipping",
                event.eventId
            )
            return
        }

        processedEventRepository.save(
            ProcessedEventJpaEntity.create(
                eventId = event.eventId,
                eventType = event.eventType,
            )
        )

        val existing = reconciliationRepository
            .findByDeliveryId(event.deliveryId)

        if (existing != null) {
            val updated = existing.applySettlement(
                settlementId = event.settlementId,
                amount =  event.driverAmount,
                currency = event.currency)
            reconciliationRepository.update(updated)

            logger.info("Process settlement reconciliation updated: {}", updated)

            return
        }

        val reconciliation = Reconciliation(
            settlementId = event.settlementId,
            deliveryId = event.deliveryId,
            expectedAmount = event.driverAmount,
            currency = event.currency
        )

        reconciliationRepository.create(reconciliation)

        logger.info("Process settlement reconciliation created: {}", reconciliation)
    }

    companion object {
        private val logger =
            LoggerFactory.getLogger(
                ProcessSettlementCreatedHandler::class.java
            )
    }
}