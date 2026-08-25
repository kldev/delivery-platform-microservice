package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.StartDeliveryCommand
import com.pl.platform.common.messaging.event.delivery.DeliveryEvent
import com.pl.platform.common.messaging.event.delivery.DeliveryStartedEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository
import com.pl.platform.svc.test.fixture.DeliveryTestFactory
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import java.util.UUID
import kotlin.test.Test

class StartDeliveryHandlerTest {
    private val deliveryRepository = mockk<DeliveryRepository>()
    private val outboxRepository = mockk<OutboxRepository>()

    private val handler = StartDeliveryHandler(
        deliveryRepository,
        outboxRepository
    )

    @Test
    fun `should start delivery and outbox event`() {
        every {
            deliveryRepository.update(any())
        } just Runs

        val delivery = DeliveryTestFactory.create()
        delivery.confirm()
        delivery.assign(UUID.randomUUID())
        delivery.pickup()

        every {
            deliveryRepository.findById(delivery.id)
        } returns delivery

        every {
            outboxRepository.save(any())
        } just Runs

        handler.handle(
            StartDeliveryCommand(
                deliveryId = delivery.id,
            )
        )

        verify(exactly = 1) {
            deliveryRepository.update(any())
        }

        val eventSlot = slot<DeliveryEvent>()

        verify(exactly = 1) {
            outboxRepository.save(capture(eventSlot))
        }

        assertThat(eventSlot.captured)
            .isInstanceOf(DeliveryStartedEvent::class.java)

        val event =
            eventSlot.captured as DeliveryStartedEvent

        assertThat(event.deliveryId)
            .isEqualTo(delivery.id.value)
    }
}
