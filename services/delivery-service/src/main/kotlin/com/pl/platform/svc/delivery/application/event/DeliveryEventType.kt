package com.pl.platform.svc.delivery.application.event

enum class DeliveryEventType(
    val value: String
) {
    CREATED("delivery.created"),
    CONFIRMED("delivery.confirmed"),
    ASSIGNED("delivery.assigned"),
    PICKED_UP("delivery.picked_up"),
    STARTED("delivery.started"),
    COMPLETED("delivery.completed"),
    CANCELLED("delivery.cancelled")
}