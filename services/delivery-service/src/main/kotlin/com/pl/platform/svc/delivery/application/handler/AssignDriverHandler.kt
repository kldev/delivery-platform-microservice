package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.exception.EntityNotFoundException
import com.pl.platform.common.exception.EntityType
import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.AssignDriverCommand
import com.pl.platform.common.messaging.event.delivery.DeliveryAssignedEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository
import com.pl.platform.svc.driver.port.DriverRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.util.UUID

@Service
class AssignDriverHandler(
    val deliveryRepository: DeliveryRepository,
    val driverRepository: DriverRepository,
    val outboxRepository: OutboxRepository,
    val random: SecureRandom = SecureRandom()
) {

    @Transactional
    fun handle(command: AssignDriverCommand) {
        val delivery = deliveryRepository.findById(command.deliveryId) ?:
            throw EntityNotFoundException(EntityType.DELIVERY, command.deliveryId)

        val driverId = command.driverId ?: randomDriver()

        delivery.assign(driverId)
        deliveryRepository.update(delivery)

        val event = DeliveryAssignedEvent(deliveryId = delivery.id.value,
            driverId = driverId)

        outboxRepository.save(event)

    }

    fun randomDriver() : UUID {
        val all = driverRepository.getAll()

        if (all.isEmpty()) {
            throw IllegalStateException("No drivers found")
        }

        return all[(random.nextInt(all.size))].id.value
    }
}