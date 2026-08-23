package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.CreateDeliveryCommand
import com.pl.platform.svc.delivery.application.event.DeliveryCreatedEvent
import com.pl.platform.svc.delivery.application.event.DeliveryEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository
import com.pl.platform.svc.pricing.service.DeliveryPricingService

import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CreateDeliveryHandlerTest {

    private val deliveryRepository = mockk<DeliveryRepository>()
    private val outboxRepository = mockk<OutboxRepository>()
    private val pricingService = DeliveryPricingService();

    private val handler = CreateDeliveryHandler(
        deliveryRepository,
        outboxRepository,
        pricingService = pricingService
    )

    @Test
    fun `should create delivery and outbox event`() {
        every {
            deliveryRepository.create(any())
        } just Runs

        every {
            outboxRepository.save(any())
        } just Runs

        handler.handle(
            CreateDeliveryCommand(
                pickupAddress = "Test 1",
                deliveryAddress = "Test 2",
                distanceKm = BigDecimal("120.55"),
            )
        )

        verify(exactly = 1) {
            deliveryRepository.create(any())
        }

        val eventSlot = slot<DeliveryEvent>()

        verify(exactly = 1) {
            outboxRepository.save(capture(eventSlot))
        }

        assertThat(eventSlot.captured)
            .isInstanceOf(DeliveryCreatedEvent::class.java)

        val event =
            eventSlot.captured as DeliveryCreatedEvent

        assertThat(event.price)
            .isGreaterThan(BigDecimal.ZERO)
    }
}