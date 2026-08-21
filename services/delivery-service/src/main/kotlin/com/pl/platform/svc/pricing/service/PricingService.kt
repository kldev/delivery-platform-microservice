package com.pl.platform.svc.pricing.service

import org.springframework.stereotype.Service
import java.math.BigDecimal


@Service
class PricingService {

    fun calculatePricing(distance: BigDecimal): BigDecimal {
        return distance.multiply(BigDecimal.valueOf(1.45));
    }
}