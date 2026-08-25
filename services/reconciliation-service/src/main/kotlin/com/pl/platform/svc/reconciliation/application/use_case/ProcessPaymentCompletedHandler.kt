package com.pl.platform.svc.reconciliation.application.use_case


import com.pl.platform.common.messaging.event.payments.PaymentCompletedEvent
import com.pl.platform.svc.messaging.adapter.persistence.ProcessedEventJpaEntity
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataProcessedEventRepository
import com.pl.platform.svc.reconciliation.domain.Reconciliation
import com.pl.platform.svc.reconciliation.port.ReconciliationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProcessPaymentCompletedHandler(
    private val reconciliationRepository: ReconciliationRepository,
    private val processedEventRepository: SpringDataProcessedEventRepository,
) {

    @Transactional
    fun handle(event: PaymentCompletedEvent) {

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

        val reconciliation =
            reconciliationRepository.findByDeliveryId(event.deliveryId)

        if (reconciliation == null) {
            val pending = Reconciliation(
                deliveryId = event.deliveryId,
                paymentId = event.paymentId,
                externalTransactionId = event.externalTransactionId,
                expectedAmount = event.amount,
                actualAmount = event.amount,
                currency = event.currency,
                difference = java.math.BigDecimal.ZERO,
                status = com.pl.platform.svc.reconciliation.domain.ReconciliationStatus.PENDING
            )

            reconciliationRepository.create(pending)
            logger.info("Process payment reconciliation created: {}", pending)

            return
        }

        val updated = reconciliation.applyPayment(
            paymentId = event.paymentId,
            externalTransactionId = event.externalTransactionId,
            actualAmount = event.amount,
            currency = event.currency,
        )
        reconciliationRepository.update(
         updated
        )

        logger.info("Process payment reconciliation updated: {}", updated)
    }

    companion object {
        private val logger =
            LoggerFactory.getLogger(
                ProcessPaymentCompletedHandler::class.java
            )
    }
}