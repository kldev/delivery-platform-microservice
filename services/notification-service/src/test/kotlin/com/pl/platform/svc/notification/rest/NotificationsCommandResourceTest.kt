package com.pl.platform.svc.notification.rest

import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.notification.adapter.rest.model.CreateNotificationRequest
import com.pl.platform.svc.notification.adapter.rest.model.NotificationResponse
import com.pl.platform.svc.notification.domain.NotificationChannel
import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.ContentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

import io.restassured.RestAssured.given

@QuarkusTest
class NotificationsCommandResourceTest : BaseIntegrationTest() {

    @Test
    fun `should create notification`() {

        val eventId = UUID.randomUUID()
        val request = CreateNotificationRequest(
            eventId = eventId,
            eventType = "DeliveryCreated",
            recipient = "user@example.com",
            channel = NotificationChannel.EMAIL,
            payload = "{\"deliveryId\":\"123\"}"
        )

        val response =
            given()
                .contentType(ContentType.JSON)
                .body(request)
                .`when`()
                .post("/api/notifications")
                .then()
                .statusCode(201)
                .extract()
                .`as`(NotificationResponse::class.java)

        assertEquals(eventId, response.eventId)
        assertEquals("DeliveryCreated", response.eventType)
        assertEquals("user@example.com", response.recipient)
        assertEquals(NotificationChannel.EMAIL, response.channel)
    }

    @Test
    fun `should reject invalid notification request`() {
        given()
            .contentType(ContentType.JSON)
            .body(
                """
            {
              "eventId": null,
              "eventType": "",
              "recipient": "",
              "channel": null,
              "payload": ""
            }
            """.trimIndent()
            )
            .`when`()
            .post("/api/notifications")
            .then()
            .statusCode(400)
    }

    @Test
    fun `should reject malformed json`() {

        given()
            .contentType(ContentType.JSON)
            .body(
                """
            {
              "eventId":
            }
            """.trimIndent()
            )
            .`when`()
            .post("/api/notifications")
            .then()
            .statusCode(400)
    }
}