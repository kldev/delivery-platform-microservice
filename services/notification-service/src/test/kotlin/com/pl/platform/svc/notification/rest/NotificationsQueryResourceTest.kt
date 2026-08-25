package com.pl.platform.svc.notification.rest

import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.notification.domain.NotificationStatus
import com.pl.platform.svc.notification.fixture.NotificationFixture
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import jakarta.inject.Inject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId

@QuarkusTest
class NotificationsQueryResourceTest : BaseIntegrationTest() {

    @Inject
    lateinit var fixture: NotificationFixture

    @BeforeEach
    fun setUp() {
        cleanDatabase()
    }

    @Test
    fun `should return notifications`() {

        fixture.create()
        fixture.create()

        given()
            .`when`()
            .get("/api/notifications")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", org.hamcrest.Matchers.equalTo(2))
    }

    @Test
    fun `should filter notifications by status`() {

        fixture.create(
            status = NotificationStatus.PENDING
        )

        fixture.create(
            status = NotificationStatus.SENT
        )

        given()
            .queryParam("status", "SENT")
            .`when`()
            .get("/api/notifications")
            .then()
            .statusCode(200)
            .body("content.size()", org.hamcrest.Matchers.equalTo(1))
            .body(
                "content.[0].status",
                org.hamcrest.Matchers.equalTo("SENT")
            )
    }

    @Test
    fun `should filter notifications by date range and paginate`() {

        val timezone = ZoneId.of("Europe/Warsaw")
        val date = LocalDate.of(2026, 8, 25)

        fixture.create(
            createdAt = date
                .atTime(10, 0)
                .atZone(timezone)
                .toInstant()
        )

        fixture.create(
            createdAt = date
                .atTime(11, 0)
                .atZone(timezone)
                .toInstant()
        )

        given()
            .queryParam("from", "2026-08-25")
            .queryParam("to", "2026-08-25")
            .queryParam("limit", 1)
            .queryParam("offset", 0)
            .`when`()
            .get("/api/notifications")
            .then()
            .statusCode(200)
            .body("content.size()", org.hamcrest.Matchers.equalTo(1))
    }
}