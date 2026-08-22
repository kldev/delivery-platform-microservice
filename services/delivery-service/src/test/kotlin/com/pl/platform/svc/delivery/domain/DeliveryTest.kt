package com.pl.platform.svc.delivery.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals

class DeliveryTest {

    private val driverId = UUID.randomUUID()

    @Test
    fun `should create delivery with CREATED status`() {
        val delivery = Delivery.create(
            pickupAddress = "Opole",
            deliveryAddress = "Wrocław",
            price = BigDecimal("200.99")
        )

        assertThat(delivery.driverId).isNull();
        assertEquals("Opole", delivery.pickupAddress)
        assertEquals("Wrocław", delivery.deliveryAddress)
        assertEquals(DeliveryStatus.CREATED, delivery.status)
    }

    @Test
    fun `should move delivery through complete lifecycle`() {
        val delivery = createDelivery()

        delivery.confirm()
        delivery.assign(driverId)

        assertEquals(
            DeliveryStatus.ASSIGNED,
            delivery.status
        )

        assertEquals(
            driverId,
            delivery.driverId
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

        delivery.confirm()
        delivery.assign(driverId)
        delivery.cancel()

        assertEquals(
            DeliveryStatus.CANCELLED,
            delivery.status
        )
    }

    @Test
    fun `should not assign delivery twice`() {
        val delivery = createDelivery()
        delivery.confirm()
        delivery.assign(driverId)

        assertThrows<IllegalArgumentException> {
            delivery.assign(driverId)
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
        delivery.confirm()
        delivery.assign(driverId)

        assertThrows<IllegalArgumentException> {
            delivery.startTransit()
        }
    }

    @Test
    fun `should not deliver before transit`() {
        val delivery = createDelivery()

        delivery.confirm()
        delivery.assign(driverId)
        delivery.pickup()

        assertThrows<IllegalArgumentException> {
            delivery.deliver()
        }
    }

    @Test
    fun `should not cancel delivered delivery`() {
        val delivery = createDelivery()

        delivery.confirm()
        delivery.assign(driverId)
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
                pickupAddress = " ",
                deliveryAddress = "Wrocław",
                price = BigDecimal("200.99")
            )
        }
    }

    @Test
    fun `should reject blank delivery address`() {
        assertThrows<IllegalArgumentException> {
            Delivery.create(
                pickupAddress = "Opole",
                deliveryAddress = " ",
                price = BigDecimal("200.99")
            )
        }
    }

    private fun createDelivery(): Delivery =
        Delivery.create(
            pickupAddress = "Opole",
            deliveryAddress = "Wrocław",
            price = BigDecimal("200.99")
        )
}