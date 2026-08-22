package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.exception.EntityNotFoundException
import com.pl.platform.common.exception.EntityType
import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.StartDeliveryCommand
import com.pl.platform.svc.delivery.application.event.DeliveryStartedEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StartDeliveryHandler(
    private val repository: DeliveryRepository,
    private val outboxRepository: OutboxRepository
) {
    @Transactional
    fun handle(command: StartDeliveryCommand) {
        val delivery = repository.findById(command.deliveryId) ?: throw EntityNotFoundException(
            EntityType.DELIVERY,
            command.deliveryId
        );

        delivery.startTransit();
        repository.update(delivery);

        val event = DeliveryStartedEvent(deliveryId = delivery.id.value);

        outboxRepository.save(event)
    }
}