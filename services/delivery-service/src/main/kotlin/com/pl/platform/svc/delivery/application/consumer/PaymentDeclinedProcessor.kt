package com.pl.platform.svc.delivery.application.consumer

import com.pl.platform.common.messaging.event.payments.PaymentDeclinedEvent
import com.pl.platform.svc.delivery.application.command.CancelDeliveryCommand
import com.pl.platform.svc.delivery.application.handler.CancelDeliveryHandler
import com.pl.platform.svc.delivery.domain.DeliveryId
import com.pl.platform.svc.messaging.adapter.persistence.ProcessedEventJpaEntity
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataProcessedEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentDeclinedProcessor(
    private val cancelDeliveryHandler: CancelDeliveryHandler,
    private val processedEventRepository: SpringDataProcessedEventRepository,
) {

    @Transactional
    fun process(event: PaymentDeclinedEvent) {

        if (processedEventRepository.existsByEventId(event.eventId)) {
            logger.info(
                "Event {} already processed, skipping",
                event.eventId
            )
            return
        }

        cancelDeliveryHandler.handle(
            CancelDeliveryCommand(
                deliveryId = DeliveryId(event.deliveryId),
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
                PaymentDeclinedProcessor::class.java
            )
    }
}