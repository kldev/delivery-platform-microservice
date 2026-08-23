package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.delivery.application.command.AssignDriverCommand
import com.pl.platform.svc.delivery.application.event.DeliveryAssignedEvent
import com.pl.platform.svc.delivery.application.event.DeliveryEvent
import com.pl.platform.svc.delivery.port.DeliveryRepository
import com.pl.platform.svc.driver.port.DriverRepository
import com.pl.platform.svc.test.fixture.DeliveryTestFactory
import com.pl.platform.svc.test.fixture.DriverTestFactory
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.shaded.com.google.common.collect.ImmutableList

class AssignDriverHandlerTest {
    private val deliveryRepository = mockk<DeliveryRepository>()
    private val outboxRepository = mockk<OutboxRepository>()
    private val driverRepository = mockk<DriverRepository>()

    private val handler = AssignDriverHandler(
        deliveryRepository = deliveryRepository,
        outboxRepository = outboxRepository,
        driverRepository = driverRepository,
    )

    @Test
    fun `should assign random driver to delivery and outbox event`() {
        val driver = DriverTestFactory.create();
        every {
            driverRepository.getAll()
        } returns ImmutableList.of(driver)

        val delivery = DeliveryTestFactory.create()
        delivery.confirm()

        every {
            deliveryRepository.update(any())
        } just Runs

        every {
            deliveryRepository.findById(delivery.id)
        } returns delivery

        every {
            outboxRepository.save(any())
        } just Runs

        val command = AssignDriverCommand(
            deliveryId = delivery.id,
            driverId = null
        )

        handler.handle(command)

        verify(exactly = 1) {
            deliveryRepository.update(any())
        }

        val eventSlot = slot<DeliveryEvent>()

        verify(exactly = 1) {
            outboxRepository.save(capture(eventSlot))
        }

        assertThat(eventSlot.captured)
            .isInstanceOf(DeliveryAssignedEvent::class.java)

        val event =
            eventSlot.captured as DeliveryAssignedEvent


        assertThat(event.driverId)
            .isEqualTo(driver.id.value)

    }

    @Test
    fun `should assign driver to delivery and outbox event`() {
        val driver = DriverTestFactory.create();

        val delivery = DeliveryTestFactory.create()
        delivery.confirm()

        every {
            deliveryRepository.update(any())
        } just Runs

        every {
            deliveryRepository.findById(delivery.id)
        } returns delivery

        every {
            outboxRepository.save(any())
        } just Runs

        val command = AssignDriverCommand(
            deliveryId = delivery.id,
            driverId = driver.id.value
        )

        handler.handle(command)

        verify(exactly = 1) {
            deliveryRepository.update(any())
        }

        val eventSlot = slot<DeliveryEvent>()

        verify(exactly = 1) {
            outboxRepository.save(capture(eventSlot))
        }

        assertThat(eventSlot.captured)
            .isInstanceOf(DeliveryAssignedEvent::class.java)

        val event =
            eventSlot.captured as DeliveryAssignedEvent


        assertThat(event.driverId)
            .isEqualTo(driver.id.value)

    }
}