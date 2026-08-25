package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.exception.EntityNotFoundException
import com.pl.platform.common.exception.EntityType
import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.ConfirmDeliveryCommand
import com.pl.platform.common.messaging.event.delivery.DeliveryConfirmedEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConfirmDeliveryHandler(var repository: DeliveryRepository,
    val outboxRepository: OutboxRepository
)  {

    @Transactional
    fun handle(command: ConfirmDeliveryCommand) {
        val delivery = repository.findById(command.deliveryId) ?: throw EntityNotFoundException(
            EntityType.DELIVERY,
            command.deliveryId
        );

        delivery.confirm();
        repository.update(delivery);

        val event = DeliveryConfirmedEvent(deliveryId = delivery.id.value,
            price = delivery.price);

        outboxRepository.save(event)
    }
}