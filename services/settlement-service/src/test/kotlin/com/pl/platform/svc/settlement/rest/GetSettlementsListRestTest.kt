package com.pl.platform.svc.settlement.rest

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.BaseRestIntegrationTest
import com.pl.platform.svc.settlement.adapter.persistence.SpringDataSettlementRepository
import com.pl.platform.svc.settlement.domain.Settlement

import com.pl.platform.svc.test.fixture.SettlementDatabaseFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.client.expectBody
import java.util.UUID
import kotlin.test.assertNotNull

class GetSettlementsListRestTest : BaseRestIntegrationTest() {

    @Autowired
    private lateinit var settlementRepository: SpringDataSettlementRepository

    @Autowired
    private lateinit var settlementDatabaseFixture: SettlementDatabaseFixture

    @BeforeEach
    fun cleanUp() {
        settlementRepository.deleteAll()
    }

    @Test
    fun `should return settlements`() {

        settlementDatabaseFixture.create(
            driverFullName = "John Connor",
        )

        settlementDatabaseFixture.create(
            driverFullName = "Sarah Connor",
        )

        val response =
            restTestClient
                .get()
                .uri(url("/api/settlements"))
                .exchange()
                .expectStatus()
                .isOk
                .expectBody<SliceResponse<Settlement>>()
                .returnResult()
                .responseBody

        assertNotNull(response)

        assertThat(response.content)
            .hasSize(2)

        assertThat(response.hasNext)
            .isFalse()
    }

    @Test
    fun `should search settlements by driver name`() {

        settlementDatabaseFixture.create(
            driverFullName = "John Connor",
        )

        settlementDatabaseFixture.create(
            driverFullName = "Sarah Connor",
        )

        val response =
            restTestClient
                .get()
                .uri(
                    url("/api/settlements?search=John")
                )
                .exchange()
                .expectStatus()
                .isOk
                .expectBody<SliceResponse<Settlement>>()
                .returnResult()
                .responseBody

        assertNotNull(response)

        assertThat(response.content)
            .hasSize(1)

        assertThat(response.content[0].driverFullName)
            .isEqualTo("John Connor")
    }

    @Test
    fun `should search settlements case insensitive`() {

        settlementDatabaseFixture.create(
            driverFullName = "John Connor",
        )

        val response =
            restTestClient
                .get()
                .uri(
                    url("/api/settlements?search=jOhN")
                )
                .exchange()
                .expectStatus()
                .isOk
                .expectBody<SliceResponse<Settlement>>()
                .returnResult()
                .responseBody

        assertNotNull(response)

        assertThat(response.content)
            .hasSize(1)

        assertThat(response.content[0].driverFullName)
            .isEqualTo("John Connor")
    }

    @Test
    fun `should filter settlements by driver id`() {

        val driverId = UUID.randomUUID()

        settlementDatabaseFixture.create(
            driverId = driverId,
            driverFullName = "John Connor",
        )

        settlementDatabaseFixture.create(
            driverId = UUID.randomUUID(),
            driverFullName = "Sarah Connor",
        )

        val response =
            restTestClient
                .get()
                .uri(
                    url("/api/settlements?driverId=$driverId")
                )
                .exchange()
                .expectStatus()
                .isOk
                .expectBody<SliceResponse<Settlement>>()
                .returnResult()
                .responseBody

        assertNotNull(response)

        assertThat(response.content)
            .hasSize(1)

        assertThat(response.content[0].driverId)
            .isEqualTo(driverId)
    }

    @Test
    fun `should filter settlements by delivery id`() {

        val deliveryId = UUID.randomUUID()

        settlementDatabaseFixture.create(
            deliveryId = deliveryId,
            driverFullName = "John Connor",
        )

        settlementDatabaseFixture.create(
            deliveryId = UUID.randomUUID(),
            driverFullName = "Sarah Connor",
        )

        val response =
            restTestClient
                .get()
                .uri(
                    url("/api/settlements?deliveryId=$deliveryId")
                )
                .exchange()
                .expectStatus()
                .isOk
                .expectBody<SliceResponse<Settlement>>()
                .returnResult()
                .responseBody

        assertNotNull(response)

        assertThat(response.content)
            .hasSize(1)

        assertThat(response.content[0].deliveryId)
            .isEqualTo(deliveryId)
    }

    @Test
    fun `should combine settlement filters`() {

        val driverId = UUID.randomUUID()

        settlementDatabaseFixture.create(
            driverId = driverId,
            driverFullName = "John Connor",
        )

        settlementDatabaseFixture.create(
            driverId = driverId,
            driverFullName = "Sarah Connor",
        )

        settlementDatabaseFixture.create(
            driverId = UUID.randomUUID(),
            driverFullName = "John Smith",
        )

        val response =
            restTestClient
                .get()
                .uri(
                    url(
                        "/api/settlements" +
                                "?search=John" +
                                "&driverId=$driverId"
                    )
                )
                .exchange()
                .expectStatus()
                .isOk
                .expectBody<SliceResponse<Settlement>>()
                .returnResult()
                .responseBody

        assertNotNull(response)

        assertThat(response.content)
            .hasSize(1)

        assertThat(response.content[0].driverFullName)
            .isEqualTo("John Connor")

        assertThat(response.content[0].driverId)
            .isEqualTo(driverId)
    }

    @Test
    fun `should return empty result when settlement does not exist`() {

        val response =
            restTestClient
                .get()
                .uri(url("/api/settlements?search=Unknown"))
                .exchange()
                .expectStatus()
                .isOk
                .expectBody<SliceResponse<Settlement>>()
                .returnResult()
                .responseBody

        assertNotNull(response)

        assertThat(response.content)
            .isEmpty()

        assertThat(response.hasNext)
            .isFalse()
    }

    @Test
    fun `should paginate settlements`() {

        repeat(3) {
            settlementDatabaseFixture.create(
                driverFullName = "Driver $it",
            )
        }

        val response =
            restTestClient
                .get()
                .uri(
                    url("/api/settlements?page=0&size=2")
                )
                .exchange()
                .expectStatus()
                .isOk
                .expectBody<SliceResponse<Settlement>>()
                .returnResult()
                .responseBody

        assertNotNull(response)

        assertThat(response.content)
            .hasSize(2)

        assertThat(response.hasNext)
            .isTrue()
    }
}