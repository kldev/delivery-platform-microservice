package com.pl.platform.svc.test.fixture
import com.pl.platform.svc.delivery.domain.Delivery
import java.math.BigDecimal
import java.util.UUID

object DeliveryTestFactory {

    fun create(
        price: BigDecimal = BigDecimal("100.00"),
        pickupAddress: String = "Opole, Krakowska 10",
        deliveryAddress: String = "Wrocław, Rynek 1"
    ): Delivery =
        Delivery.create(
            pickupAddress = pickupAddress,
            deliveryAddress = deliveryAddress,
            price = price,
            distanceKm = BigDecimal("50")
        )
}