package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.PickupDeliveryCommand
import com.pl.platform.common.messaging.event.delivery.DeliveryEvent
import com.pl.platform.common.messaging.event.delivery.DeliveryPickedUpEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository
import com.pl.platform.svc.test.fixture.DeliveryTestFactory
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class PickupDeliveryHandlerTest {
    private val deliveryRepository = mockk<DeliveryRepository>()
    private val outboxRepository = mockk<OutboxRepository>()

    private val handler = PickupDeliveryHandler(
        deliveryRepository,
        outboxRepository
    )

    @Test
    fun `should confirm delivery and outbox event`() {
        every {
            deliveryRepository.update(any())
        } just Runs

        val delivery = DeliveryTestFactory.create()
        delivery.confirm()
        delivery.assign(UUID.randomUUID())

        every {
            deliveryRepository.findById(delivery.id)
        } returns delivery

        every {
            outboxRepository.save(any())
        } just Runs

        handler.handle(
            PickupDeliveryCommand(
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
            .isInstanceOf(DeliveryPickedUpEvent::class.java)

        val event =
            eventSlot.captured as DeliveryPickedUpEvent

        assertThat(event.deliveryId)
            .isEqualTo(delivery.id.value)
        assertThat(event.aggregateId)
            .isEqualTo(delivery.id.value)
    }
}