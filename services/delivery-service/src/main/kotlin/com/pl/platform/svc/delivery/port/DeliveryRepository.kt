package com.pl.platform.svc.delivery.port

import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.domain.DeliveryId

interface DeliveryRepository {

    fun findById(id: DeliveryId): Delivery?

    fun create(delivery: Delivery)

    fun update(delivery: Delivery)

    fun existsByPhoneNumber(phoneNumber: String): Boolean
}