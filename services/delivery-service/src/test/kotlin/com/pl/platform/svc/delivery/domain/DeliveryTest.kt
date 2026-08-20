package com.pl.platform.svc.delivery.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals

class DeliveryTest {

    private val driverId = UUID.randomUUID()

    @Test
    fun `should create delivery with CREATED status`() {
        val delivery = Delivery.create(
            driverId = driverId,
            pickupAddress = "Opole",
            deliveryAddress = "Wrocław"
        )

        assertEquals(driverId, delivery.driverId)
        assertEquals("Opole", delivery.pickupAddress)
        assertEquals("Wrocław", delivery.deliveryAddress)
        assertEquals(DeliveryStatus.CREATED, delivery.status)
    }

    @Test
    fun `should move delivery through complete lifecycle`() {
        val delivery = createDelivery()

        delivery.assign()

        assertEquals(
            DeliveryStatus.ASSIGNED,
            delivery.status
        )

        delivery.pickup()

        assertEquals(
            DeliveryStatus.PICKED_UP,
            delivery.status
        )

        delivery.startTransit()

        assertEquals(
            DeliveryStatus.IN_TRANSIT,
            delivery.status
        )

        delivery.deliver()

        assertEquals(
            DeliveryStatus.DELIVERED,
            delivery.status
        )
    }

    @Test
    fun `should cancel delivery from CREATED`() {
        val delivery = createDelivery()

        delivery.cancel()

        assertEquals(
            DeliveryStatus.CANCELLED,
            delivery.status
        )
    }

    @Test
    fun `should cancel delivery from ASSIGNED`() {
        val delivery = createDelivery()

        delivery.assign()
        delivery.cancel()

        assertEquals(
            DeliveryStatus.CANCELLED,
            delivery.status
        )
    }

    @Test
    fun `should not assign delivery twice`() {
        val delivery = createDelivery()

        delivery.assign()

        assertThrows<IllegalArgumentException> {
            delivery.assign()
        }
    }

    @Test
    fun `should not pick up delivery before assignment`() {
        val delivery = createDelivery()

        assertThrows<IllegalArgumentException> {
            delivery.pickup()
        }
    }

    @Test
    fun `should not start transit before pickup`() {
        val delivery = createDelivery()

        delivery.assign()

        assertThrows<IllegalArgumentException> {
            delivery.startTransit()
        }
    }

    @Test
    fun `should not deliver before transit`() {
        val delivery = createDelivery()

        delivery.assign()
        delivery.pickup()

        assertThrows<IllegalArgumentException> {
            delivery.deliver()
        }
    }

    @Test
    fun `should not cancel delivered delivery`() {
        val delivery = createDelivery()

        delivery.assign()
        delivery.pickup()
        delivery.startTransit()
        delivery.deliver()

        assertThrows<IllegalArgumentException> {
            delivery.cancel()
        }
    }

    @Test
    fun `should not cancel already cancelled delivery`() {
        val delivery = createDelivery()

        delivery.cancel()

        assertThrows<IllegalArgumentException> {
            delivery.cancel()
        }
    }

    @Test
    fun `should reject blank pickup address`() {
        assertThrows<IllegalArgumentException> {
            Delivery.create(
                driverId = driverId,
                pickupAddress = " ",
                deliveryAddress = "Wrocław"
            )
        }
    }

    @Test
    fun `should reject blank delivery address`() {
        assertThrows<IllegalArgumentException> {
            Delivery.create(
                driverId = driverId,
                pickupAddress = "Opole",
                deliveryAddress = " "
            )
        }
    }

    private fun createDelivery(): Delivery =
        Delivery.create(
            driverId = driverId,
            pickupAddress = "Opole",
            deliveryAddress = "Wrocław"
        )
}