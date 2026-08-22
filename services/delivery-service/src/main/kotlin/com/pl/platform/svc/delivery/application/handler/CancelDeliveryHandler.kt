package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.exception.EntityNotFoundException
import com.pl.platform.common.exception.EntityType
import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.CancelDeliveryCommand
import com.pl.platform.svc.delivery.application.event.DeliveryCancelledEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CancelDeliveryHandler(
    private val deliveryRepository: DeliveryRepository,
    private val outboxRepository: OutboxRepository
) {
    @Transactional
    fun handle(command: CancelDeliveryCommand) {
        val delivery = deliveryRepository.findById(command.deliveryId) ?:
        throw EntityNotFoundException(EntityType.DELIVERY, command.deliveryId)

        delivery.cancel()
        deliveryRepository.update(delivery)

        val event = DeliveryCancelledEvent(deliveryId = command.deliveryId.value)
        outboxRepository.save(event)
    }
}