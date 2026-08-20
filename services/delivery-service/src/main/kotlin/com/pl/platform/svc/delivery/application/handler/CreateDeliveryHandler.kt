package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.svc.delivery.application.command.CreateDeliveryCommand
import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.port.DeliveryRepository
import org.springframework.stereotype.Service

@Service
class CreateDeliveryHandler(
    private val deliveryRepository: DeliveryRepository
) {

    fun handle(command: CreateDeliveryCommand): Delivery {

        val delivery = Delivery.create(
            driverId = command.driverId,
            pickupAddress = command.pickupAddress.trim(),
            deliveryAddress = command.deliveryAddress.trim()
        )

        deliveryRepository.create(delivery)

        return delivery
    }
}