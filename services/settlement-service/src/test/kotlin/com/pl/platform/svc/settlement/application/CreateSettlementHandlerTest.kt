package com.pl.platform.svc.settlement.application

import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.settlement.application.create_settlement.CreateSettlementCommand
import com.pl.platform.svc.settlement.application.create_settlement.CreateSettlementHandler
import com.pl.platform.svc.settlement.application.create_settlement.SettlementCalculator
import com.pl.platform.svc.settlement.application.event.SettlementCreatedEvent
import com.pl.platform.svc.settlement.domain.Settlement
import com.pl.platform.svc.settlement.domain.SettlementRate
import com.pl.platform.svc.settlement.port.SettlementRateRepository
import com.pl.platform.svc.settlement.port.SettlementRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class CreateSettlementHandlerTest {

    private val settlementRepository =
        mockk<SettlementRepository>()

    private val settlementRateRepository =
        mockk<SettlementRateRepository>()

    private val outboxRepository =
        mockk<OutboxRepository>()

    private val calculator =
        mockk<SettlementCalculator>()

    private val handler =
        CreateSettlementHandler(
            settlementRepository = settlementRepository,
            settlementRateRepository = settlementRateRepository,
            outboxRepository = outboxRepository,
            calculator = calculator,
        )

    @Test
    fun `should calculate create settlement and publish event`() {
        val command = CreateSettlementCommand(
            deliveryId = UUID.randomUUID(),
            driverId = UUID.randomUUID(),
            driverFullName = "John Connor",
            deliveryAmount = BigDecimal("100.00"),
            currency = "EUR",
            distanceKm = BigDecimal("150.00"),
            completedAt = Instant.parse("2026-08-22T18:00:00Z"),
        )

        val rates = listOf(
            mockk<SettlementRate>(),
            mockk<SettlementRate>(),
        )

        val settlement = Settlement.create(
            deliveryId = command.deliveryId,
            driverId = command.driverId,
            driverFullName = command.driverFullName,
            deliveryAmount = command.deliveryAmount,
            currency = command.currency,
            basePercentage = BigDecimal("70.00"),
            nightPercentage = BigDecimal("0.00"),
            weekendPercentage = BigDecimal("0.00"),
            distancePercentage = BigDecimal("10.00"),
            totalPercentage = BigDecimal("80.00"),
            driverAmount = command.deliveryAmount,
            distanceKm = command.distanceKm,
            completedAt = command.completedAt,
        )

        every {
            settlementRateRepository.findActive()
        } returns rates

        every {
            calculator.calculate(
                command = command,
                rates = rates,
            )
        } returns settlement

        every {
            settlementRepository.create(settlement)
        } just Runs

        every {
            outboxRepository.save(any())
        } just Runs

        handler.handle(command)

        verify(exactly = 1) {
            settlementRateRepository.findActive()
        }

        verify(exactly = 1) {
            calculator.calculate(
                command = command,
                rates = rates,
            )
        }

        verify(exactly = 1) {
            settlementRepository.create(settlement)
        }

        verify(exactly = 1) {
            outboxRepository.save(any())
        }
    }

    @Test
    fun `should publish settlement created event`() {
        val command = CreateSettlementCommand(
            deliveryId = UUID.randomUUID(),
            driverId = UUID.randomUUID(),
            driverFullName = "John Connor",
            deliveryAmount = BigDecimal("100.00"),
            currency = "EUR",
            distanceKm = BigDecimal("150.00"),
            completedAt = Instant.parse("2026-08-22T18:00:00Z"),
        )

        val rates = emptyList<SettlementRate>()

        val settlement = Settlement.create(
            deliveryId = command.deliveryId,
            driverId = command.driverId,
            driverFullName = command.driverFullName,
            deliveryAmount = command.deliveryAmount,
            currency = command.currency,
            basePercentage = BigDecimal("70.00"),
            nightPercentage = BigDecimal("5.00"),
            weekendPercentage = BigDecimal.ZERO,
            distancePercentage = BigDecimal("10.00"),
            totalPercentage = BigDecimal("85.00"),
            driverAmount = BigDecimal("85.00"),
            distanceKm = command.distanceKm,
            completedAt = command.completedAt,
        )

        every {
            settlementRateRepository.findActive()
        } returns rates

        every {
            calculator.calculate(
                command = command,
                rates = rates,
            )
        } returns settlement

        every {
            settlementRepository.create(settlement)
        } just Runs

        val eventSlot =
            slot<SettlementCreatedEvent>()

        every {
            outboxRepository.save(capture(eventSlot))
        } just Runs

        val result = handler.handle(command)

        assertThat(result)
            .isSameAs(settlement)

        verify(exactly = 1) {
            outboxRepository.save(any())
        }

        assertThat(eventSlot.captured)
            .isInstanceOf(SettlementCreatedEvent::class.java)

        assertThat(eventSlot.captured.deliveryId)
            .isEqualTo(command.deliveryId)

        assertThat(eventSlot.captured.driverId)
            .isEqualTo(command.driverId)

        assertThat(eventSlot.captured.driverAmount)
            .isEqualByComparingTo(BigDecimal("85.00"))

        assertThat(eventSlot.captured.totalPercentage)
            .isEqualByComparingTo(BigDecimal("85.00"))
    }
}