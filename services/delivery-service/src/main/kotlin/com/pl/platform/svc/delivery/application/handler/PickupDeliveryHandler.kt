package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.exception.EntityNotFoundException
import com.pl.platform.common.exception.EntityType
import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.PickupDeliveryCommand
import com.pl.platform.common.messaging.event.delivery.DeliveryPickedUpEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PickupDeliveryHandler(
    private val deliveryRepository: DeliveryRepository,
    private val outboxRepository: OutboxRepository
) {

    @Transactional
    fun handle(command: PickupDeliveryCommand) {
        val delivery = deliveryRepository.findById(command.deliveryId) ?:
        throw EntityNotFoundException(EntityType.DELIVERY, command.deliveryId)

        delivery.pickup()
        deliveryRepository.update(delivery)

        val event = DeliveryPickedUpEvent(deliveryId = command.deliveryId.value)
        outboxRepository.save(event)
    }
}