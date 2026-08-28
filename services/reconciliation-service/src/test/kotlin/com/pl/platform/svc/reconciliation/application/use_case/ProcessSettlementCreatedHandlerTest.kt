package com.pl.platform.svc.reconciliation.application.use_case

import com.pl.platform.common.messaging.event.settlement.SettlementCreatedEvent
import com.pl.platform.svc.messaging.adapter.persistence.ProcessedEventJpaEntity
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataProcessedEventRepository
import com.pl.platform.svc.reconciliation.domain.Reconciliation
import com.pl.platform.svc.reconciliation.domain.ReconciliationStatus
import com.pl.platform.svc.reconciliation.port.ReconciliationRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

class ProcessSettlementCreatedHandlerTest {

    private val reconciliationRepository =
        mockk<ReconciliationRepository>()

    private val processedEventRepository =
        mockk<SpringDataProcessedEventRepository>()

    private val handler = ProcessSettlementCreatedHandler(
        reconciliationRepository = reconciliationRepository,
        processedEventRepository = processedEventRepository
    )

    @Test
    fun `should skip already processed event`() {
        val event = settlementCreatedEvent()

        every {
            processedEventRepository.existsByEventId(event.eventId)
        } returns true

        handler.handle(event)

        verify(exactly = 1) {
            processedEventRepository.existsByEventId(event.eventId)
        }

        verify(exactly = 0) {
            processedEventRepository.save(any())
        }

        verify(exactly = 0) {
            reconciliationRepository.findByDeliveryId(any())
        }

        verify(exactly = 0) {
            reconciliationRepository.create(any())
        }

        verify(exactly = 0) {
            reconciliationRepository.update(any())
        }
    }

    @Test
    fun `should create reconciliation when reconciliation does not exist`() {
        val event = settlementCreatedEvent()

        every {
            processedEventRepository.existsByEventId(event.eventId)
        } returns false

        every {
            processedEventRepository.save(any())
        } answers {
            firstArg<ProcessedEventJpaEntity>()
        }

        every {
            reconciliationRepository.findByDeliveryId(event.deliveryId)
        } returns null

        every {
            reconciliationRepository.create(any())
        } just runs

        handler.handle(event)

        verify(exactly = 1) {
            processedEventRepository.existsByEventId(event.eventId)
        }

        verify(exactly = 1) {
            processedEventRepository.save(
                match {
                    it.eventId == event.eventId &&
                            it.eventType == event.eventType
                }
            )
        }

        verify(exactly = 1) {
            reconciliationRepository.findByDeliveryId(
                event.deliveryId
            )
        }

        verify(exactly = 1) {
            reconciliationRepository.create(
                match {
                    it.deliveryId == event.deliveryId &&
                            it.settlementId == event.settlementId &&
                            it.expectedAmount == event.driverAmount &&
                            it.currency == event.currency &&
                            it.status == ReconciliationStatus.PENDING
                }
            )
        }

        verify(exactly = 0) {
            reconciliationRepository.update(any())
        }
    }

    @Test
    fun `should update existing reconciliation`() {
        val event = settlementCreatedEvent(amount = BigDecimal("120.00"))

        val existing = Reconciliation(
            deliveryId = event.deliveryId,
            settlementId = event.settlementId,
            expectedAmount = BigDecimal("120.00"),
            actualAmount = BigDecimal("100.00"),
            currency = event.currency,
            status = ReconciliationStatus.RECONCILED,
            difference = BigDecimal("20.00")
        )

        every {
            processedEventRepository.existsByEventId(event.eventId)
        } returns false

        every {
            processedEventRepository.save(any())
        } answers {
            firstArg<ProcessedEventJpaEntity>()
        }

        every {
            reconciliationRepository.findByDeliveryId(event.deliveryId)
        } returns existing

        every {
            reconciliationRepository.update(any())
        } just runs

        handler.handle(event)

        verify(exactly = 1) {
            processedEventRepository.save(
                match {
                    it.eventId == event.eventId &&
                            it.eventType == event.eventType
                }
            )
        }

        verify(exactly = 1) {
            reconciliationRepository.findByDeliveryId(
                event.deliveryId
            )
        }

        verify(exactly = 1) {
            reconciliationRepository.update(any())
        }

        verify(exactly = 0) {
            reconciliationRepository.create(any())
        }

        assertEquals(
            event.settlementId,
            existing.settlementId
        )

        assertEquals(
            event.driverAmount,
            existing.expectedAmount
        )

        assertEquals(
            event.currency,
            existing.currency
        )
    }

    @Test
    fun `should create processed event before creating reconciliation`() {
        val event = settlementCreatedEvent()

        every {
            processedEventRepository.existsByEventId(event.eventId)
        } returns false

        every {
            processedEventRepository.save(any())
        } answers {
            firstArg<ProcessedEventJpaEntity>()
        }

        every {
            reconciliationRepository.findByDeliveryId(event.deliveryId)
        } returns null

        every {
            reconciliationRepository.create(any())
        } just runs

        handler.handle(event)

        verifyOrder {
            processedEventRepository.existsByEventId(event.eventId)
            processedEventRepository.save(any())
            reconciliationRepository.findByDeliveryId(event.deliveryId)
            reconciliationRepository.create(any())
        }
    }

    private fun settlementCreatedEvent(
        eventId: UUID = UUID.randomUUID(),
        settlementId: UUID = UUID.randomUUID(),
        deliveryId: UUID = UUID.randomUUID(),
        amount: BigDecimal = BigDecimal("125.50"),
        currency: String = "EUR"
    ) = SettlementCreatedEvent(
        settlementId = settlementId,
        driverId = UUID.randomUUID(),
        deliveryId = deliveryId,
        driverAmount = amount,
        deliveryAmount = amount.multiply(BigDecimal("1.3")),
        currency = currency,
        basePercentage = BigDecimal("70.0"),
        nightPercentage = BigDecimal("5.0"),
        weekendPercentage = BigDecimal("2.0"),
        distancePercentage = BigDecimal("3.0"),
        totalPercentage = BigDecimal("80.0"),
        distanceKm = BigDecimal("25.0"),
        completedAt = Instant.now()
    )
}

