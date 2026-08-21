package com.pl.platform.svc.messaging

import com.pl.platform.common.messaging.OutboxStatus
import com.pl.platform.common.messaging.event.TestEvent
import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.BaseIntegrationTest
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
            TestEvent(
                module = "ledger", eventType = "ledger.event",
            )

        outboxRepository.save(event)

        val entity =
            springDataOutboxRepository.findByEventId(event.eventId)
                ?: throw IllegalArgumentException("Event with id ${event.eventId} not found")

        assertThat(entity.module)
            .isEqualTo("ledger")

        assertThat(entity.aggregateId)
            .isEqualTo(event.aggregateId)

        assertThat(entity.eventType)
            .isEqualTo("ledger.event")


        assertThat(entity.payload)
            .contains(event.eventId.toString())

        assertThat(entity.status)
            .isEqualTo(OutboxStatus.PENDING)

        assertThat(entity.attempts)
            .isZero()
    }
}