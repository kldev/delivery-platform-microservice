package com.pl.platform.svc.pricing.service

import org.springframework.stereotype.Service
import java.math.BigDecimal


@Service
class DeliveryPricingService {

    fun calculate(distanceKm: BigDecimal): BigDecimal {
        require(distanceKm > BigDecimal.ZERO) {
            "Distance must be greater than zero."
        }

        val amount =
            when {
                distanceKm <= BigDecimal("10") ->
                    BigDecimal("5.00")

                distanceKm <= BigDecimal("50") ->
                    BigDecimal("10.00")

                distanceKm <= BigDecimal("100") ->
                    BigDecimal("20.00")

                else ->
                    BigDecimal("50.00")
            }

        return amount;
    }
}