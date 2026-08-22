package com.pl.platform.svc.delivery.rest

import com.pl.platform.svc.BaseRestIntegrationTest
import com.pl.platform.svc.delivery.adapter.persistence.DeliveryStatusJpa
import com.pl.platform.svc.delivery.adapter.persistence.SpringDataDeliveryRepository
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import com.pl.platform.svc.messaging.adapter.publisher.InMemoryEventPublisher
import com.pl.platform.svc.test.fixture.DeliveryDatabaseFixture
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.TimeUnit

class CancelDeliveryRestTest : BaseRestIntegrationTest() {

    @Autowired
    private lateinit var deliveryRepository: SpringDataDeliveryRepository

    @Autowired
    private lateinit var deliveryDatabaseFixture: DeliveryDatabaseFixture

    @Autowired
    lateinit var eventPublisher: InMemoryEventPublisher

    @BeforeEach
    fun cleanUp() {
        deliveryRepository.deleteAll()
        eventPublisher.clear()
    }

    @Test
    fun `should cancel delivery`() {
        val deliveryId = deliveryDatabaseFixture.create()

        restTestClient
            .put()
            .uri(url("/api/deliveries/$deliveryId/cancel"))
            .exchange()
            .expectStatus()
            .isNoContent

        val delivery = deliveryRepository.findById(deliveryId)

        assertThat(delivery)
            .isPresent

        assertThat(delivery.get().status)
            .isEqualTo(DeliveryStatusJpa.CANCELLED)
    }

    @Test
    fun `should publish delivery cancelled event`() {
        val deliveryId = deliveryDatabaseFixture.create()

        restTestClient
            .put()
            .uri(url("/api/deliveries/$deliveryId/cancel"))
            .exchange()
            .expectStatus()
            .isNoContent

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted {
                assertThat(eventPublisher.events())
                    .anyMatch {
                        it.eventType == "delivery.cancelled"
                    }
            }
    }
}