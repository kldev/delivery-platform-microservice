package com.pl.platform.svc.delivery.domain

enum class DeliveryStatus {
    CREATED,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}