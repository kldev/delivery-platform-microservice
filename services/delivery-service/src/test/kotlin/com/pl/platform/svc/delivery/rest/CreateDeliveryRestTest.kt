package com.pl.platform.svc.delivery.rest


import com.pl.platform.svc.BaseRestIntegrationTest
import com.pl.platform.svc.delivery.adapter.persistence.SpringDataDeliveryRepository
import com.pl.platform.svc.delivery.adapter.rest.request.CreateDeliveryRequest
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryResponse
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import com.pl.platform.svc.test.fixture.DriverDatabaseFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.client.expectBody
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreateDeliveryRestTest : BaseRestIntegrationTest() {

    @Autowired
    private lateinit var deliveryRepository: SpringDataDeliveryRepository

    @Autowired
    private lateinit var driverDatabaseFixture: DriverDatabaseFixture

    @BeforeEach
    fun cleanUp() {
        deliveryRepository.deleteAll()
    }

    @Test
    fun `should create delivery`() {
        val driverId = driverDatabaseFixture.create(phoneNumber = "123456789")

        val request = CreateDeliveryRequest(
            driverId = driverId,
            pickupAddress = "Opole, Krakowska 10",
            deliveryAddress = "Wrocław, Rynek 1"
        )

        val response =
            restTestClient
                .post()
                .uri(url("/api/deliveries"))
                .body(request)
                .exchange()
                .expectStatus()
                .isCreated
                .expectBody<DeliveryResponse>()
                .returnResult()
                .responseBody

        assertNotNull(response)

        assertNotNull(response.id)
        assertEquals(driverId, response.driverId)
        assertEquals(
            "Opole, Krakowska 10",
            response.pickupAddress
        )
        assertEquals(
            "Wrocław, Rynek 1",
            response.deliveryAddress
        )
        assertEquals(
            DeliveryStatus.CREATED,
            response.status
        )
    }

    @Test
    fun `should reject blank pickup address`() {
        val driverId = driverDatabaseFixture.create(phoneNumber = "123456783")

        val request = CreateDeliveryRequest(
            driverId = driverId,
            pickupAddress = "",
            deliveryAddress = "Wrocław, Rynek 1"
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
        val driverId = driverDatabaseFixture.create(phoneNumber = "123456782")
        val request = CreateDeliveryRequest(
            driverId = driverId,
            pickupAddress = "Opole, Krakowska 10",
            deliveryAddress = ""
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
    fun `should reject missing driver id`() {
        val driverId = driverDatabaseFixture.create(phoneNumber = "123452782")

        val request = CreateDeliveryRequest(
            driverId = null,
            pickupAddress = "Opole, Krakowska 10",
            deliveryAddress = "Wrocław, Rynek 1"
        )

        restTestClient
            .post()
            .uri(url("/api/deliveries"))
            .body(request)
            .exchange()
            .expectStatus()
            .isBadRequest
    }
}