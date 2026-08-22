package com.pl.platform.svc.delivery.application.consumer

import com.pl.platform.common.messaging.event.payments.PaymentPaidEvent
import com.pl.platform.svc.delivery.application.command.AssignDriverCommand
import com.pl.platform.svc.delivery.application.handler.AssignDriverHandler
import com.pl.platform.svc.delivery.domain.DeliveryId
import com.pl.platform.svc.messaging.adapter.persistence.ProcessedEventJpaEntity
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataProcessedEventRepository
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Component

@Component
class PaymentPaidProcessor(
    private val assignDriverHandler: AssignDriverHandler,
    private val processedEventRepository: SpringDataProcessedEventRepository,
) {

    @Transactional
    fun process(event: PaymentPaidEvent) {

        if (processedEventRepository.existsByEventId(event.eventId)) {
            logger.info(
                "Event {} already processed, skipping",
                event.eventId
            )
            return
        }

        assignDriverHandler.handle(
            AssignDriverCommand(
                deliveryId = DeliveryId(event.deliveryId),
                driverId = null
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
                PaymentPaidProcessor::class.java
            )
    }
}