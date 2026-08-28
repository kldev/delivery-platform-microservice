package com.pl.platform.svc.reconciliation.application.use_case

import com.pl.platform.common.messaging.event.payments.PaymentCompletedEvent
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataProcessedEventRepository
import com.pl.platform.svc.reconciliation.domain.Reconciliation
import com.pl.platform.svc.reconciliation.domain.ReconciliationStatus
import com.pl.platform.svc.reconciliation.port.ReconciliationRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class ProcessPaymentCompletedHandlerTest {

    private val reconciliationRepository =
        mockk<ReconciliationRepository>()

    private val processedEventRepository =
        mockk<SpringDataProcessedEventRepository>()

    private val handler = ProcessPaymentCompletedHandler(
        reconciliationRepository = reconciliationRepository,
        processedEventRepository = processedEventRepository
    )

    @Test
    fun `should skip event when event was already processed`() {
        val event = paymentCompletedEvent()

        every {
            processedEventRepository.existsByEventId(event.eventId)
        } returns true

        handler.handle(event)

        verify(exactly = 1) {
            processedEventRepository.existsByEventId(event.eventId)
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
        val event = paymentCompletedEvent()

        every {
            processedEventRepository.existsByEventId(event.eventId)
        } returns false

        every {
            reconciliationRepository.findByDeliveryId(event.deliveryId)
        } returns null

        every {
            reconciliationRepository.create(any())
        } just runs

        every {
            processedEventRepository.save(any())
        }  returnsArgument 0

        handler.handle(event)

        verify(exactly = 1) {
            reconciliationRepository.findByDeliveryId(event.deliveryId)
        }

        verify(exactly = 1) {
            reconciliationRepository.create(
                match {
                    it.deliveryId == event.deliveryId &&
                            it.paymentId == event.paymentId &&
                            it.externalTransactionId == event.externalTransactionId &&
                            it.expectedAmount == event.amount &&
                            it.actualAmount == event.amount &&
                            it.currency == event.currency &&
                            it.difference == BigDecimal.ZERO &&
                            it.status == ReconciliationStatus.PENDING
                }
            )
        }

        verify(exactly = 0) {
            reconciliationRepository.update(any())
        }
    }

    @Test
    fun `should update reconciliation when reconciliation exists`() {
        val event = paymentCompletedEvent()

        val reconciliation = Reconciliation(
            deliveryId = event.deliveryId,
            settlementId = UUID.randomUUID(),
            paymentId = null,
            externalTransactionId = null,
            expectedAmount = BigDecimal("100.00"),
            actualAmount = null,
            currency = event.currency,
            status = ReconciliationStatus.PENDING
        )

        every {
            processedEventRepository.existsByEventId(event.eventId)
        } returns false

        every {
            processedEventRepository.save(any())
        }  returnsArgument 0

        every {
            reconciliationRepository.findByDeliveryId(event.deliveryId)
        } returns reconciliation

        every {
            reconciliationRepository.update(any())
        } just runs

        handler.handle(event)

        verify(exactly = 1) {
            reconciliationRepository.findByDeliveryId(event.deliveryId)
        }

        verify(exactly = 1) {
            reconciliationRepository.update(
                match {
                    it.deliveryId == event.deliveryId &&
                            it.paymentId == event.paymentId &&
                            it.externalTransactionId == event.externalTransactionId &&
                            it.actualAmount == event.amount &&
                            it.currency == event.currency
                }
            )
        }

        verify(exactly = 0) {
            reconciliationRepository.create(any())
        }
    }

    @Test
    fun `should not update reconciliation when event was already processed`() {
        val event = paymentCompletedEvent()

        every {
            processedEventRepository.existsByEventId(event.eventId)
        } returns true

        handler.handle(event)

        verify(exactly = 1) {
            processedEventRepository.existsByEventId(event.eventId)
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

    private fun paymentCompletedEvent(
        deliveryId: UUID = UUID.randomUUID(),
        paymentId: UUID = UUID.randomUUID(),
        amount: BigDecimal = BigDecimal("125.50"),
        currency: String = "EUR",
        externalTransactionId: String = "EXT-123"
    ) = PaymentCompletedEvent(
        deliveryId = deliveryId,
        paymentId = paymentId,
        externalTransactionId = externalTransactionId,
        amount = amount,
        currency = currency,

        )
}

