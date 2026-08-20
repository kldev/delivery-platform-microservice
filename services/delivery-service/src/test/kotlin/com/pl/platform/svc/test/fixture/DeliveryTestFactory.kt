package com.pl.platform.svc.test.fixture
import com.pl.platform.svc.delivery.domain.Delivery
import java.util.UUID

object DeliveryTestFactory {

    fun create(
        driverId: UUID = UUID.randomUUID(),
        pickupAddress: String = "Opole, Krakowska 10",
        deliveryAddress: String = "Wrocław, Rynek 1"
    ): Delivery =
        Delivery.create(
            driverId = driverId,
            pickupAddress = pickupAddress,
            deliveryAddress = deliveryAddress
        )
}