package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.exception.EntityNotFoundException
import com.pl.platform.common.exception.EntityType
import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.CompleteDeliveryCommand
import com.pl.platform.common.messaging.event.delivery.DeliveryCompletedEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository
import com.pl.platform.svc.driver.domain.DriverId
import com.pl.platform.svc.driver.port.DriverRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CompleteDeliveryHandler(
    private val deliveryRepository: DeliveryRepository,
    private val driverRepository: DriverRepository,
    private val outboxRepository: OutboxRepository
) {
    @Transactional
    fun handle(command: CompleteDeliveryCommand) {
        val delivery = deliveryRepository.findById(command.deliveryId) ?: throw EntityNotFoundException(
            EntityType.DELIVERY,
            command.deliveryId
        )

        val driver = driverRepository.findById(DriverId(delivery.driverId!!))
            ?: throw EntityNotFoundException(
                    EntityType.DRIVER, delivery.driverId!!)

        delivery.deliver()
        deliveryRepository.update(delivery)


        val event = DeliveryCompletedEvent(
            deliveryId = command.deliveryId.value,
            distanceKm = delivery.distanceKm,
            price = delivery.price,
            driverId = driver.id.value,
            driverFullName = driver.fullName()
        )
        outboxRepository.save(event)
    }
}