package com.pl.platform.svc.delivery.rest


import com.pl.platform.svc.BaseRestIntegrationTest
import com.pl.platform.svc.delivery.adapter.persistence.SpringDataDeliveryRepository
import com.pl.platform.svc.delivery.adapter.rest.request.CreateDeliveryRequest
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryCreateResponse
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import com.pl.platform.svc.messaging.adapter.publisher.InMemoryEventPublisher
import com.pl.platform.svc.test.fixture.DeliveryTestFactory
import com.pl.platform.svc.test.fixture.DriverDatabaseFixture
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.client.expectBody
import java.math.BigDecimal
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreateDeliveryRestTest : BaseRestIntegrationTest() {

    @Autowired
    private lateinit var deliveryRepository: SpringDataDeliveryRepository

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
    fun `should create delivery`() {
        val driverId = driverDatabaseFixture.create(phoneNumber = "123456789")

        val request = CreateDeliveryRequest(
            pickupAddress = "Opole, Krakowska 10",
            deliveryAddress = "Wrocław, Rynek 1",
            distanceKm = BigDecimal("100.00"),
            currency = "PLN"
        )

        val response =
            restTestClient
                .post()
                .uri(url("/api/deliveries"))
                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                .body(request)
                .exchange()
                .expectStatus()
                .isCreated
                .expectBody<DeliveryCreateResponse>()
                .returnResult()
                .responseBody

        assertNotNull(response)

        assertNotNull(response.id)
        assertThat(response.price).isGreaterThan(BigDecimal.ONE)

        assertEquals(
            DeliveryStatus.CREATED,
            response.status
        )
    }

    @Test
    fun `should reject blank pickup address`() {


        val request = CreateDeliveryRequest(
            pickupAddress = "",
            deliveryAddress = "Wrocław, Rynek 1",
            distanceKm = BigDecimal("200.99"),
            currency = "PLN"
        )

        restTestClient
            .post()
            .uri(url("/api/deliveries"))
            .body(request)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should reject blank delivery address`() {
        val request = CreateDeliveryRequest(
            pickupAddress = "Opole, Krakowska 10",
            deliveryAddress = "",
            distanceKm = BigDecimal("100.00"),
            currency = "PLN"
        )

        restTestClient
            .post()
            .uri(url("/api/deliveries"))
            .body(request)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should publish delivery created event through in-memory publisher`() {

        val request = CreateDeliveryRequest(
            pickupAddress = "Opole, Krakowska 10",
            deliveryAddress = "Brzeg, Długa 10",
            distanceKm = BigDecimal("100.00"),
            currency = "PLN"
        )

        restTestClient
            .post()
            .uri("/api/deliveries")
            .header("X-Idempotency-Key", UUID.randomUUID().toString())
            .body(
                request
            )
            .exchange()
            .expectStatus()
            .isCreated

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted {

                assertThat(eventPublisher.events())
                    .anyMatch {
                        it.eventType == "delivery.created"
                    }
            }
    }


}