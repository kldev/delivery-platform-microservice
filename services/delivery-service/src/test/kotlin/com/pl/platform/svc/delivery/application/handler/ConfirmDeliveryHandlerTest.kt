package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.ConfirmDeliveryCommand
import com.pl.platform.common.messaging.event.delivery.DeliveryConfirmedEvent
import com.pl.platform.common.messaging.event.delivery.DeliveryEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository
import com.pl.platform.svc.test.fixture.DeliveryTestFactory
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ConfirmDeliveryHandlerTest {
    private val deliveryRepository = mockk<DeliveryRepository>()
    private val outboxRepository = mockk<OutboxRepository>()

    private val handler = ConfirmDeliveryHandler(
        deliveryRepository,
        outboxRepository
    )

    @Test
    fun `should confirm delivery and outbox event`() {
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
            ConfirmDeliveryCommand(
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
            .isInstanceOf(DeliveryConfirmedEvent::class.java)

        val event =
            eventSlot.captured as DeliveryConfirmedEvent

        assertThat(event.price)
            .isGreaterThan(BigDecimal.ZERO)
    }
}