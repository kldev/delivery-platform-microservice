package com.pl.platform.svc.reconciliation.application.use_case

import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.integration.event.PaymentCompletedEvent
import com.pl.platform.svc.messaging.adapter.persistence.ProcessedEventJpaEntity
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataProcessedEventRepository
import com.pl.platform.svc.reconciliation.adapter.persistence.ReconciliationJpaEntity
import com.pl.platform.svc.reconciliation.adapter.persistence.SpringDataReconciliationRepository
import com.pl.platform.svc.reconciliation.domain.ReconciliationStatus

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProcessPaymentCompletedHandlerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var handler: ProcessPaymentCompletedHandler

    @Autowired
    private lateinit var reconciliationRepository: SpringDataReconciliationRepository

    @Autowired
    private lateinit var processedEventRepository: SpringDataProcessedEventRepository

    @Test
    fun `should create reconciliation when payment event is received`() {
        val event = PaymentCompletedEvent(
            eventId = UUID.randomUUID(),
            deliveryId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            externalTransactionId = "EXT-${UUID.randomUUID()}",
            amount = BigDecimal("125.50"),
            currency = "EUR"
        )

        handler.handle(event)

        val reconciliation =
            reconciliationRepository.findByDeliveryId(event.deliveryId)

        assertNotNull(reconciliation)

        assertEquals(event.deliveryId, reconciliation.deliveryId)
        assertEquals(event.paymentId, reconciliation.paymentId)
        assertEquals(
            event.externalTransactionId,
            reconciliation.externalTransactionId
        )

        assertAmount(event.amount, reconciliation.actualAmount)
        assertEquals(event.currency, reconciliation.currency)

        assertAmount(
            BigDecimal.ZERO,
            reconciliation.difference
        )

        assertEquals(
            ReconciliationStatus.PENDING,
            reconciliation.status
        )
    }

    @Test
    fun `should update existing reconciliation when payment event is received`() {
        val deliveryId = UUID.randomUUID()

        val reconciliation = reconciliationRepository.save(
            ReconciliationJpaEntity(
                id = UUID.randomUUID(),
                deliveryId = deliveryId,
                settlementId = UUID.randomUUID(),
                paymentId = null,
                externalTransactionId = null,
                expectedAmount = BigDecimal("100.00"),
                actualAmount = null,
                difference = null,
                currency = "EUR",
                status = ReconciliationStatus.PENDING,
                reconciledAt = null
            )
        )

        val event = PaymentCompletedEvent(
            eventId = UUID.randomUUID(),
            deliveryId = deliveryId,
            paymentId = UUID.randomUUID(),
            externalTransactionId = "EXT-123",
            amount = BigDecimal("100.00"),
            currency = "EUR"
        )

        handler.handle(event)

        val updated =
            reconciliationRepository.findByDeliveryId(deliveryId)

        assertNotNull(updated)

        assertEquals(
            event.paymentId,
            updated.paymentId
        )

        assertEquals(
            event.externalTransactionId,
            updated.externalTransactionId
        )

        assertAmount(
            event.amount,
            updated.actualAmount
        )

        assertAmount(
            BigDecimal.ZERO,
            updated.difference
        )

        assertEquals(
            ReconciliationStatus.RECONCILED,
            updated.status
        )
    }

    @Test
    fun `should ignore already processed payment event`() {
        val event = PaymentCompletedEvent(
            eventId = UUID.randomUUID(),
            deliveryId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            externalTransactionId = "EXT-123",
            amount = BigDecimal("125.50"),
            currency = "EUR"
        )

        processedEventRepository.save(
            ProcessedEventJpaEntity(
                eventId = event.eventId,
                eventType = event.eventType,
                processedAt = Instant.now(),
            )
        )

        handler.handle(event)

        assertNull(
            reconciliationRepository.findByDeliveryId(event.deliveryId)
        )
    }
}
