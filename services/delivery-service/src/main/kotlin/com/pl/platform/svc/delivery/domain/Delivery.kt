package com.pl.platform.svc.delivery.domain

import java.util.UUID

class Delivery private constructor(
    val id: DeliveryId,
    val driverId: UUID,
    val pickupAddress: String,
    val deliveryAddress: String,
    private var currentStatus: DeliveryStatus
) {

    val status: DeliveryStatus
        get() = currentStatus

    companion object {

        fun create(
            driverId: UUID,
            pickupAddress: String,
            deliveryAddress: String
        ): Delivery {
            require(pickupAddress.isNotBlank()) {
                "Pickup address must not be blank"
            }

            require(deliveryAddress.isNotBlank()) {
                "Delivery address must not be blank"
            }

            return Delivery(
                id = DeliveryId.new(),
                driverId = driverId,
                pickupAddress = pickupAddress,
                deliveryAddress = deliveryAddress,
                currentStatus = DeliveryStatus.CREATED
            )
        }

        fun reconstitute(
            id: DeliveryId,
            driverId: UUID,
            pickupAddress: String,
            deliveryAddress: String,
            status: DeliveryStatus
        ): Delivery =
            Delivery(
                id = id,
                driverId = driverId,
                pickupAddress = pickupAddress,
                deliveryAddress = deliveryAddress,
                currentStatus = status
            )
    }

    fun assign() {
        require(currentStatus == DeliveryStatus.CREATED) {
            "Delivery can only be assigned from CREATED status"
        }

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