package com.pl.platform.svc.delivery.domain

enum class DeliveryStatus {
    CREATED,
    CONFIRMED,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}