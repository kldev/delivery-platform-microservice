package com.pl.platform.svc.delivery.application

import com.pl.platform.svc.delivery.application.command.*
import com.pl.platform.svc.delivery.application.handler.*
import com.pl.platform.svc.delivery.domain.DeliveryId
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeliveryService(
    private val createDeliveryHandler: CreateDeliveryHandler,
    private val confirmDeliveryHandler: ConfirmDeliveryHandler,
    private val assignDriverHandler: AssignDriverHandler,
    private val cancelDeliveryHandler: CancelDeliveryHandler,
    private val pickupDeliveryHandler: PickupDeliveryHandler,
    private val startDeliveryHandler: StartDeliveryHandler,
    private val completeDeliveryHandler: CompleteDeliveryHandler,
) {

    fun create(command: CreateDeliveryCommand) =
        createDeliveryHandler.handle(command)

    fun confirm(deliveryId: UUID) =
        confirmDeliveryHandler.handle(
            ConfirmDeliveryCommand(
                deliveryId = DeliveryId(deliveryId)
            )
        )

    fun assign(
        deliveryId: UUID,
        driverId: UUID?
    ) =
        assignDriverHandler.handle(
            AssignDriverCommand(
                deliveryId = DeliveryId(deliveryId),
                driverId = driverId
            )
        )

    fun cancel(deliveryId: UUID) =
        cancelDeliveryHandler.handle(
            CancelDeliveryCommand(
                deliveryId = DeliveryId(deliveryId)
            )
        )

    fun pickup(deliveryId: UUID) =
        pickupDeliveryHandler.handle(
            PickupDeliveryCommand(
                deliveryId = DeliveryId(deliveryId)
            )
        )

    fun start(deliveryId: UUID) =
        startDeliveryHandler.handle(
            StartDeliveryCommand(
                deliveryId = DeliveryId(deliveryId)
            )
        )

    fun complete(deliveryId: UUID) =
        completeDeliveryHandler.handle(
            CompleteDeliveryCommand(
                deliveryId = DeliveryId(deliveryId)
            )
        )
}