package com.pl.platform.svc.notification.delivery


import com.github.dockerjava.api.model.DriverStatus
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.pl.platform.svc.delivery.client.DeliveryClient
import com.pl.platform.svc.delivery.client.model.DeliveryStatus
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

@QuarkusTest
@QuarkusTestResource(WireMockResource::class)
class DeliveryClientTest {

    @Inject
    @RestClient
    lateinit var client: DeliveryClient

    @Test
    fun `should get deliveries by status and delivery id`() {
        val deliveryId = UUID.randomUUID()
        val driverId = UUID.randomUUID()

        WireMockResource.wireMock.stubFor(
            get(urlPathEqualTo("/api/deliveries"))
                .withQueryParam("status", equalTo("ASSIGNED"))
                .withQueryParam("deliveryId", equalTo(deliveryId.toString()))
                .willReturn(
                    okJson(
                        """{
                            "content": [
                            {
                              "id": "$deliveryId",
                              "status": "ASSIGNED",
                              "driverId": "$driverId"
                            }]
                        }
                        """.trimIndent()
                    )
                )
        )

        val result = client.getDeliveries(
            status = DeliveryStatus.ASSIGNED,
            deliveryId = deliveryId,
        )

        assertEquals(1, result.content.size)
        assertEquals(deliveryId, result.content[0].id)
        assertEquals(DeliveryStatus.ASSIGNED, result.content[0].status)
        assertEquals(driverId, result.content[0].driverId)

        WireMockResource.wireMock.verify(
            getRequestedFor(urlPathEqualTo("/api/deliveries"))
                .withQueryParam("status", equalTo("ASSIGNED"))
                .withQueryParam("deliveryId", equalTo(deliveryId.toString()))
        )
    }

    @Test
    fun `should get deliveries without delivery id`() {
        WireMockResource.wireMock.stubFor(
            get(urlPathEqualTo("/api/deliveries"))
                .withQueryParam("status", equalTo("ASSIGNED"))
                .willReturn(
                    okJson(
                        """
                        [
                          {
                            "id": "${UUID.randomUUID()}",
                            "status": "ASSIGNED",
                            "driverId": null
                          }
                        ]
                        """.trimIndent()
                    )
                )
        )

        val result = client.getDeliveries(
            status = DeliveryStatus.ASSIGNED,
            deliveryId = null,
        )

        assertEquals(1, result.content.size)

        WireMockResource.wireMock.verify(
            getRequestedFor(urlPathEqualTo("/api/deliveries"))
                .withQueryParam("status", equalTo("ASSIGNED"))
        )
    }

    @Test
    fun `should get drivers by driver id`() {
        val driverId = UUID.randomUUID()

        WireMockResource.wireMock.stubFor(
            get(urlPathEqualTo("/api/drivers"))
                .withQueryParam("driverId", equalTo(driverId.toString()))
                .willReturn(
                    okJson(
                        """
                        [
                          {
                            "id": "$driverId",
                            "status": "AVAILABLE"
                          }
                        ]
                        """.trimIndent()
                    )
                )
        )

        val result = client.getDrivers(driverId)

        assertEquals(1, result.size)
        assertEquals(driverId, result[0].id)


        WireMockResource.wireMock.verify(
            getRequestedFor(urlPathEqualTo("/api/drivers"))
                .withQueryParam("driverId", equalTo(driverId.toString()))
        )
    }

    @Test
    fun `should get all drivers without driver id`() {
        WireMockResource.wireMock.stubFor(
            get(urlPathEqualTo("/api/drivers"))
                .willReturn(
                    okJson(
                        """
                        [
                          {
                            "id": "${UUID.randomUUID()}",
                            "status": "AVAILABLE"
                          }
                        ]
                        """.trimIndent()
                    )
                )
        )

        val result = client.getDrivers(null)

        assertEquals(1, result.size)

        WireMockResource.wireMock.verify(
            getRequestedFor(urlPathEqualTo("/api/drivers"))
        )
    }
}