package com.pl.platform.svc.messaging


import com.pl.platform.common.messaging.OutboxStatus
import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.messaging.adapter.persistence.SpringDataOutboxRepository
import com.pl.platform.svc.settlement.application.event.SettlementCompletedEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class OutboxPersistenceTest : BaseIntegrationTest() {

    @Autowired
    lateinit var outboxRepository: OutboxRepository

    @Autowired
    lateinit var springDataOutboxRepository: SpringDataOutboxRepository

    @Test
    fun `should persist outbox message`() {

        val event =
            SettlementCompletedEvent(
                id = UUID.randomUUID()
            )

        outboxRepository.save(event)

        val entity =
            springDataOutboxRepository.findByEventId(event.eventId)
                ?: throw IllegalArgumentException("Event with id ${event.eventId} not found")

        assertThat(entity.module)
            .isEqualTo("settlement")

        assertThat(entity.aggregateId)
            .isEqualTo(event.id)

        assertThat(entity.eventType)
            .isEqualTo(event.eventType)


        assertThat(entity.payload)
            .contains(event.id.toString())

        assertThat(entity.status)
            .isEqualTo(OutboxStatus.PENDING)

        assertThat(entity.attempts)
            .isZero()
    }
}