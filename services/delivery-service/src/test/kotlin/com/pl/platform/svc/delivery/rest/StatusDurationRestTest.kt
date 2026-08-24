package com.pl.platform.svc.delivery.rest

import com.pl.platform.svc.BaseRestIntegrationTest
import com.pl.platform.svc.delivery.adapter.persistence.DeliveryStatusJpa
import com.pl.platform.svc.test.fixture.DeliveryDatabaseFixture
import com.pl.platform.svc.test.fixture.DriverDatabaseFixture
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate


class StatusDurationRestTest : BaseRestIntegrationTest() {


    @Autowired
    lateinit var deliveryDatabaseFixture: DeliveryDatabaseFixture

    @Autowired
    lateinit var driverDatabaseFixture: DriverDatabaseFixture

    @Transactional
    @BeforeEach
    fun cleanUp() {
        cleanDatabase()
        seedTest()
    }

    @Transactional
    fun seedTest() {
        deliveryDatabaseFixture.create()
        deliveryDatabaseFixture.create()

        val assigned = deliveryDatabaseFixture.create()
        val driver = driverDatabaseFixture.create()

        deliveryDatabaseFixture.changeStatus(
            assigned,
            DeliveryStatusJpa.ASSIGNED,
            driverId = driver
        )
    }

    @Test
    fun `should return delivery status duration`() {
        restTestClient
            .get()
            .uri("/api/deliveries/status-duration")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content").isNotEmpty
    }

    @Test
    fun `should return status duration for selected delivery`() {
        val deliveryId = deliveryDatabaseFixture.create()

        restTestClient
            .get()
            .uri("/api/deliveries/status-duration?deliveryId=$deliveryId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content.length()").isEqualTo(1)
            .jsonPath("$.content[0].deliveryId").isEqualTo(deliveryId.toString())
            .jsonPath("$.content[0].status").isEqualTo("CREATED")
    }

    @Test
    fun `should return created and assigned status duration`() {
        val deliveryId = deliveryDatabaseFixture.create()
        val driverId = driverDatabaseFixture.create()

        deliveryDatabaseFixture.changeStatus(
            deliveryId,
            DeliveryStatusJpa.ASSIGNED,
            driverId = driverId
        )

        restTestClient
            .get()
            .uri("/api/deliveries/status-duration?deliveryId=$deliveryId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content.length()").isEqualTo(2)
            .jsonPath("$.content[0].status").isEqualTo("CREATED")
            .jsonPath("$.content[1].status").isEqualTo("ASSIGNED")
    }

    @Test
    fun `should respect size parameter`() {
        restTestClient
            .get()
            .uri("/api/deliveries/status-duration?size=1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content.length()").isEqualTo(1)
    }

    @Test
    fun `should respect page parameter`() {

        restTestClient
            .get()
            .uri("/api/deliveries/status-duration?size=1&page=1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content.length()").isEqualTo(1)
    }

    @Test
    fun `should reject size greater than 500`() {

        restTestClient
            .get()
            .uri("/api/deliveries/status-duration?size=600")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is5xxServerError
    }
}