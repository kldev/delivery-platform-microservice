package com.pl.platform.svc.delivery.domain

import java.math.BigDecimal
import java.util.UUID

class Delivery private constructor(
    val id: DeliveryId,
    var driverId: UUID?,
    val pickupAddress: String,
    val deliveryAddress: String,
    val price: BigDecimal,
    private var currentStatus: DeliveryStatus
) {

    val status: DeliveryStatus
        get() = currentStatus

    companion object {

        fun create(
            pickupAddress: String,
            deliveryAddress: String,
            price: BigDecimal,
        ): Delivery {
            require(pickupAddress.isNotBlank()) {
                "Pickup address must not be blank"
            }

            require(deliveryAddress.isNotBlank()) {
                "Delivery address must not be blank"
            }

            return Delivery(
                id = DeliveryId.new(),
                driverId = null,
                pickupAddress = pickupAddress,
                deliveryAddress = deliveryAddress,
                currentStatus = DeliveryStatus.CREATED,
                price = price,
            )
        }

        fun reconstitute(
            id: DeliveryId,
            driverId: UUID?,
            pickupAddress: String,
            deliveryAddress: String,
            status: DeliveryStatus,
            price: BigDecimal,
        ): Delivery =
            Delivery(
                id = id,
                driverId = driverId,
                pickupAddress = pickupAddress,
                deliveryAddress = deliveryAddress,
                currentStatus = status,
                price = price,
            )
    }

    fun assign(driverId: UUID) {
        require(currentStatus == DeliveryStatus.CREATED) {
            "Delivery can only be assigned from CREATED status"
        }

        this.driverId = driverId
        currentStatus = DeliveryStatus.ASSIGNED
    }

    fun pickup() {
        require(currentStatus == DeliveryStatus.ASSIGNED) {
            "Delivery can only be picked up from ASSIGNED status"
        }

        currentStatus = DeliveryStatus.PICKED_UP
    }

    fun startTransit() {
        require(currentStatus == DeliveryStatus.PICKED_UP) {
            "Delivery can only start transit from PICKED_UP status"
        }

        currentStatus = DeliveryStatus.IN_TRANSIT
    }

    fun deliver() {
        require(currentStatus == DeliveryStatus.IN_TRANSIT) {
            "Delivery can only be delivered from IN_TRANSIT status"
        }

        currentStatus = DeliveryStatus.DELIVERED
    }

    fun cancel() {
        require(
            currentStatus == DeliveryStatus.CREATED ||
                    currentStatus == DeliveryStatus.ASSIGNED ||
                    currentStatus == DeliveryStatus.PICKED_UP ||
                    currentStatus == DeliveryStatus.IN_TRANSIT
        ) {
            "Delivery cannot be cancelled from $currentStatus status"
        }

        currentStatus = DeliveryStatus.CANCELLED
    }
}