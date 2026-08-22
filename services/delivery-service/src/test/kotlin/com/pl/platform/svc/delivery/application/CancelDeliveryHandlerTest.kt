package com.pl.platform.svc.delivery.application

import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.CancelDeliveryCommand
import com.pl.platform.svc.delivery.application.event.DeliveryCancelledEvent
import com.pl.platform.svc.delivery.application.event.DeliveryEvent
import com.pl.platform.svc.delivery.application.handler.CancelDeliveryHandler
import com.pl.platform.svc.delivery.port.DeliveryRepository
import com.pl.platform.svc.test.fixture.DeliveryTestFactory
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CancelDeliveryHandlerTest {
    private val deliveryRepository = mockk<DeliveryRepository>()
    private val outboxRepository = mockk<OutboxRepository>()

    private val handler = CancelDeliveryHandler(
        deliveryRepository,
        outboxRepository
    )

    @Test
    fun `should cancel delivery and outbox event`() {
        every {
            deliveryRepository.update(any())
        } just Runs

        val delivery = DeliveryTestFactory.create()

        every {
            deliveryRepository.findById(delivery.id)
        } returns delivery

        every {
            outboxRepository.save(any())
        } just Runs

        handler.handle(
            CancelDeliveryCommand(
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
            .isInstanceOf(DeliveryCancelledEvent::class.java)

        val event =
            eventSlot.captured as DeliveryCancelledEvent

        assertThat(event.deliveryId)
            .isEqualTo(delivery.id.value)
    }
}