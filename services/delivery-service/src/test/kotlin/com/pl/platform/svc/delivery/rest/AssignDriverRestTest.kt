package com.pl.platform.svc.delivery.rest

import com.pl.platform.svc.BaseRestIntegrationTest
import com.pl.platform.svc.delivery.adapter.persistence.DeliveryStatusJpa
import com.pl.platform.svc.delivery.adapter.persistence.SpringDataDeliveryRepository
import com.pl.platform.svc.messaging.adapter.publisher.InMemoryEventPublisher
import com.pl.platform.svc.test.fixture.DeliveryDatabaseFixture
import com.pl.platform.svc.test.fixture.DriverDatabaseFixture
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.TimeUnit

class AssignDriverRestTest : BaseRestIntegrationTest() {

    @Autowired
    private lateinit var deliveryRepository: SpringDataDeliveryRepository

    @Autowired
    private lateinit var deliveryDatabaseFixture: DeliveryDatabaseFixture

    @Autowired
    private lateinit var driverDatabaseFixture: DriverDatabaseFixture

    @Autowired
    lateinit var eventPublisher: InMemoryEventPublisher

    @BeforeEach
    fun cleanUp() {
        deliveryRepository.deleteAll()
        eventPublisher.clear()
    }

    @Test
    fun `should assign driver to delivery`() {
        val deliveryId = deliveryDatabaseFixture.create()
        val driverId = driverDatabaseFixture.create()

        deliveryDatabaseFixture.changeStatus(deliveryId, DeliveryStatusJpa.CONFIRMED)


        restTestClient
            .put()
            .uri("/api/deliveries/$deliveryId/assign?driverId=$driverId")
            .exchange()
            .expectStatus()
            .isNoContent

        val delivery = deliveryRepository.findById(deliveryId)

        assertThat(delivery)
            .isPresent

        assertThat(delivery.get().driverId)
            .isEqualTo(driverId)

        assertThat(delivery.get().status)
            .isEqualTo(DeliveryStatusJpa.ASSIGNED)
    }

    @Test
    fun `should publish delivery assigned event`() {
        val deliveryId = deliveryDatabaseFixture.create()
        deliveryDatabaseFixture.changeStatus(deliveryId, DeliveryStatusJpa.CONFIRMED)

        driverDatabaseFixture.create()
        driverDatabaseFixture.create()
        driverDatabaseFixture.create()

        restTestClient
            .put()
            .uri("/api/deliveries/$deliveryId/assign")
            .exchange()
            .expectStatus()
            .isNoContent

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted {
                assertThat(eventPublisher.events())
                    .anyMatch {
                        it.eventType == "delivery.assigned"
                    }
            }
    }
}