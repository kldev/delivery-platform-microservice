package com.pl.platform.svc.delivery.domain

import java.math.BigDecimal
import java.util.UUID

class Delivery private constructor(
    val id: DeliveryId,
    var driverId: UUID?,
    val pickupAddress: String,
    val deliveryAddress: String,
    var distanceKm: BigDecimal,
    val price: BigDecimal,
    var currency: String,
    private var currentStatus: DeliveryStatus
) {

    val status: DeliveryStatus
        get() = currentStatus

    companion object {

        fun create(
            pickupAddress: String,
            deliveryAddress: String,
            price: BigDecimal,
            distanceKm: BigDecimal,
            currency: String= "PLN"
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
                distanceKm = distanceKm,
                currency = currency
            )
        }

        fun reconstitute(
            id: DeliveryId,
            driverId: UUID?,
            pickupAddress: String,
            deliveryAddress: String,
            status: DeliveryStatus,
            price: BigDecimal,
            distanceKm: BigDecimal,
            currency: String?
        ): Delivery =
            Delivery(
                id = id,
                driverId = driverId,
                pickupAddress = pickupAddress,
                deliveryAddress = deliveryAddress,
                currentStatus = status,
                price = price,
                distanceKm = distanceKm,
                currency = currency ?: "PLN"
            )
    }

    fun confirm() {
        require(currentStatus == DeliveryStatus.CREATED) {
            "Delivery can only be confirmed from CREATED status"
        }

        currentStatus = DeliveryStatus.CONFIRMED
    }

    fun assign(driverId: UUID) {
        require(currentStatus == DeliveryStatus.CONFIRMED) {
            "Delivery can only be assigned from CONFIRMED status"
        }

        this.driverId = driverId
        currentStatus = DeliveryStatus.ASSIGNED
    }

    fun pickup() {
        require(currentStatus == DeliveryStatus.ASSIGNED) {
            "Delivery ${id.value} can only be picked up from ASSIGNED status. Curerent status = ${status.name}"
        }

        currentStatus = DeliveryStatus.PICKED_UP
    }

    fun startTransit() {
        require(currentStatus == DeliveryStatus.PICKED_UP) {
            "Delivery ${id.value} can only start transit from PICKED_UP status"
        }

        currentStatus = DeliveryStatus.IN_TRANSIT
    }

    fun deliver() {
        require(currentStatus == DeliveryStatus.IN_TRANSIT) {
            "Delivery ${id.value} can only be delivered from IN_TRANSIT status"
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