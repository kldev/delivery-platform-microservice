package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryCreateResponse
import com.pl.platform.svc.delivery.application.command.CreateDeliveryCommand
import com.pl.platform.common.messaging.event.delivery.DeliveryCreatedEvent
import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.port.DeliveryRepository
import com.pl.platform.svc.pricing.service.DeliveryPricingService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class CreateDeliveryHandler(
    private val deliveryRepository: DeliveryRepository,
    private val outboxRepository: OutboxRepository,
    private val pricingService: DeliveryPricingService
) {

    @Transactional
    fun handle(command: CreateDeliveryCommand): DeliveryCreateResponse {

        val delivery = Delivery.create(
            pickupAddress = command.pickupAddress.trim(),
            deliveryAddress = command.deliveryAddress.trim(),
            price = pricingService.calculate(command.distanceKm),
            distanceKm = command.distanceKm,
            currency = command.currency,
        )

        deliveryRepository.create(delivery)

        outboxRepository.save(
            DeliveryCreatedEvent(
                deliveryId = delivery.id.value,
                price = delivery.price,
            )
        )

        return DeliveryCreateResponse(
            delivery.id.value,
            price = delivery.price,
            status = delivery.status
        );



    }
}