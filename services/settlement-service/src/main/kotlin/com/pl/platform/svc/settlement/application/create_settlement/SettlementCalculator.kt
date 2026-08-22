package com.pl.platform.svc.settlement.application.create_settlement

import com.pl.platform.svc.settlement.domain.Settlement
import com.pl.platform.svc.settlement.domain.SettlementRate
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@Component
class SettlementCalculator {

    fun calculate(
        command: CreateSettlementCommand,
        rates: List<SettlementRate>,
    ): Settlement {

        val basePercentage =
            rate(rates, "BASE")

        val nightPercentage =
            if (isNight(command.completedAt)) {
                rateOrZero(rates, "NIGHT")
            } else {
                BigDecimal.ZERO
            }

        val weekendPercentage =
            if (isWeekend(command.completedAt)) {
                rateOrZero(rates, "WEEKEND")
            } else {
                BigDecimal.ZERO
            }

        val distancePercentage =
            if (command.distanceKm > BigDecimal("100")) {
                rateOrZero(rates, "LONG_DISTANCE")
            } else {
                BigDecimal.ZERO
            }

        val totalPercentage =
            basePercentage +
                    nightPercentage +
                    weekendPercentage +
                    distancePercentage

        val driverAmount =
            command.deliveryAmount
                .multiply(totalPercentage)
                .divide(BigDecimal("100"))

        return Settlement.create(
            deliveryId = command.deliveryId,
            driverId = command.driverId,
            driverFullName = command.driverFullName,
            deliveryAmount = command.deliveryAmount,
            currency = command.currency,
            basePercentage = basePercentage,
            nightPercentage = nightPercentage,
            weekendPercentage = weekendPercentage,
            distancePercentage = distancePercentage,
            totalPercentage = totalPercentage,
            driverAmount = driverAmount,
            distanceKm = command.distanceKm,
            completedAt = command.completedAt,
        )
    }

    private fun rate(
        rates: List<SettlementRate>,
        code: String,
    ): BigDecimal =
        rates.firstOrNull { it.code == code }
            ?.percentage
            ?: error("Settlement rate '$code' is not configured")

    private fun rateOrZero(
        rates: List<SettlementRate>,
        code: String,
    ): BigDecimal =
        rates.firstOrNull { it.code == code }
            ?.percentage
            ?: BigDecimal.ZERO

    private fun isNight(completedAt: Instant): Boolean {
        val time = completedAt
            .atZone(ZoneId.of("Europe/Warsaw"))
            .toLocalTime()

        return time >= LocalTime.of(22, 0) ||
                time < LocalTime.of(6, 0)
    }

    private fun isWeekend(completedAt: Instant): Boolean {
        val dayOfWeek = completedAt
            .atZone(ZoneId.of("Europe/Warsaw"))
            .dayOfWeek

        return dayOfWeek == DayOfWeek.SATURDAY ||
                dayOfWeek == DayOfWeek.SUNDAY
    }
}