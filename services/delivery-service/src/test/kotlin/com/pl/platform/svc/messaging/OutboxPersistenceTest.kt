package com.pl.platform.svc.messaging

import com.pl.platform.common.messaging.OutboxStatus
import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.delivery.application.event.DeliveryCreatedEvent
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataOutboxRepository


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.UUID

class OutboxPersistenceTest : BaseIntegrationTest() {

    @Autowired
    lateinit var outboxRepository: OutboxRepository

    @Autowired
    lateinit var springDataOutboxRepository: SpringDataOutboxRepository

    @Test
    fun `should persist outbox message`() {

        val event =
            DeliveryCreatedEvent(
                deliveryId = UUID.randomUUID(),
                price = BigDecimal("200.99")
            )

        outboxRepository.save(event)

        val entity =
            springDataOutboxRepository.findByEventId(event.eventId)
                ?: throw IllegalArgumentException("Event with id ${event.eventId} not found")

        assertThat(entity.module)
            .isEqualTo("delivery")

        assertThat(entity.aggregateId)
            .isEqualTo(event.deliveryId)

        assertThat(entity.eventType)
            .isEqualTo("delivery.created")


        assertThat(entity.payload)
            .contains(event.deliveryId.toString())

        assertThat(entity.status)
            .isEqualTo(OutboxStatus.PENDING)

        assertThat(entity.attempts)
            .isZero()
    }
}