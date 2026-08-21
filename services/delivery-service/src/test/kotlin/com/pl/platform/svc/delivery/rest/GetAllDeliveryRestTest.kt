package com.pl.platform.svc.delivery.rest

import com.pl.platform.svc.BaseRestIntegrationTest
import com.pl.platform.svc.delivery.adapter.persistence.DeliveryJpaEntity
import com.pl.platform.svc.delivery.adapter.persistence.SpringDataDeliveryRepository
import com.pl.platform.svc.test.fixture.DeliveryTestFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

class GetAllDeliveryRestTest : BaseRestIntegrationTest() {

    @Autowired
    lateinit var deliveryRepository: SpringDataDeliveryRepository

    @BeforeEach
    fun setUp() {
        deliveryRepository.deleteAll()
    }

    @Test
    fun `should return deliveries`() {
        val first = DeliveryTestFactory.create()
        val second = DeliveryTestFactory.create()

        deliveryRepository.save(DeliveryJpaEntity.create(first))
        deliveryRepository.save(DeliveryJpaEntity.create(second))

        restTestClient
            .get()
            .uri("/api/deliveries")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$.length()").isEqualTo(2)
    }

    @Test
    fun `should return empty list when no deliveries exist`() {
        restTestClient
            .get()
            .uri("/api/deliveries")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$.length()").isEqualTo(0)
    }
}