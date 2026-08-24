package com.pl.platform.svc.reconciliation.application.use_case

import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.integration.event.SettlementCreatedEvent
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataProcessedEventRepository
import com.pl.platform.svc.reconciliation.adapter.persistence.SpringDataReconciliationRepository
import com.pl.platform.svc.reconciliation.domain.ReconciliationStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ProcessSettlementCreatedHandlerIntegrationTest :
    BaseIntegrationTest() {

    @Autowired
    private lateinit var handler: ProcessSettlementCreatedHandler

    @Autowired
    private lateinit var reconciliationRepository:
            SpringDataReconciliationRepository

    @Autowired
    private lateinit var processedEventRepository:
            SpringDataProcessedEventRepository

    @Test
    fun `should create reconciliation and processed event`() {
        val event = settlementCreatedEvent()

        handler.handle(event)

        val reconciliation =
            reconciliationRepository.findByDeliveryId(
                event.deliveryId
            )

        assertNotNull(reconciliation)

        assertEquals(
            event.deliveryId,
            reconciliation.deliveryId
        )

        assertEquals(
            event.settlementId,
            reconciliation.settlementId
        )

        assertAmount(
            event.driverAmount,
            reconciliation.expectedAmount ?: BigDecimal.ZERO
        )

        assertEquals(
            event.currency,
            reconciliation.currency
        )

        assertEquals(
            ReconciliationStatus.PENDING,
            reconciliation.status
        )

        val processedEvent =
            processedEventRepository.findById(event.eventId).orElseThrow()

        assertNotNull(processedEvent)

        assertEquals(
            event.eventId,
            processedEvent.eventId
        )

        assertEquals(
            event.eventType,
            processedEvent.eventType
        )
    }

    @Test
    fun `should update existing reconciliation`() {
        val deliveryId = UUID.randomUUID()

        val firstEvent = settlementCreatedEvent(
            deliveryId = deliveryId,
            settlementId = UUID.randomUUID(),
            amount = BigDecimal("100.00")
        )

        handler.handle(firstEvent)

        val secondEvent = settlementCreatedEvent(
            deliveryId = deliveryId,
            settlementId = UUID.randomUUID(),
            amount = BigDecimal("125.50")
        )

        handler.handle(secondEvent)

        val reconciliation =
            reconciliationRepository.findByDeliveryId(
                deliveryId
            )

        assertNotNull(reconciliation)

        assertEquals(
            secondEvent.settlementId,
            reconciliation.settlementId
        )

        assertAmount(
            BigDecimal("125.50"),
            reconciliation.expectedAmount
        )

        assertEquals(
            secondEvent.currency,
            reconciliation.currency
        )
    }

    @Test
    fun `should not process duplicated event`() {
        val event = settlementCreatedEvent()

        handler.handle(event)
        handler.handle(event)

        val reconciliations =
            reconciliationRepository.findByDeliveryId(
                event.deliveryId
            )

        assertNotNull(reconciliations)

        val processedEvents =
            processedEventRepository.findAllByEventId(
                event.eventId
            )

        assertEquals(
            1,
            processedEvents.size
        )
    }

    @Test
    fun `should not create second reconciliation for duplicated event`() {
        val event = settlementCreatedEvent()

        handler.handle(event)

        val before =
            reconciliationRepository.findByDeliveryId(
                event.deliveryId
            )

        handler.handle(event)

        val after =
            reconciliationRepository.findByDeliveryId(
                event.deliveryId
            )

        assertNotNull(before)
        assertNotNull(after)

        assertEquals(
            before.id,
            after.id
        )

        assertEquals(
            before.settlementId,
            after.settlementId
        )

        assertEquals(
            before.expectedAmount,
            after.expectedAmount
        )
    }

    private fun settlementCreatedEvent(
        eventId: UUID = UUID.randomUUID(),
        settlementId: UUID = UUID.randomUUID(),
        deliveryId: UUID = UUID.randomUUID(),
        amount: BigDecimal = BigDecimal("125.50"),
        currency: String = "EUR"
    ) = SettlementCreatedEvent(
        eventId = eventId,
        settlementId = settlementId,
        deliveryId = deliveryId,
        deliveryAmount = amount,
        driverAmount = amount.multiply(BigDecimal("0.8")),
        currency = currency,
        driverId = UUID.randomUUID(),
    )


}
