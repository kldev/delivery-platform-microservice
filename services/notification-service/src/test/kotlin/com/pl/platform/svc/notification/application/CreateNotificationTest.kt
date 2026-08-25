package com.pl.platform.svc.notification.application

import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.notification.application.command.CreateNotification
import com.pl.platform.svc.notification.application.command.CreateNotificationCommand
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.port.NotificationRepository
import com.pl.platform.svc.notification.domain.NotificationStatus
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.UUID

@QuarkusTest
class CreateNotificationTest : BaseIntegrationTest() {

    @Inject
    lateinit var createNotification: CreateNotification

    @Inject
    lateinit var repository: NotificationRepository

    @Test
    fun `should create notification`() {
        val eventId = UUID.randomUUID()

        val command = CreateNotificationCommand(
            eventId = eventId,
            eventType = "delivery.created",
            recipient = "user@example.com",
            channel = NotificationChannel.EMAIL,
            payload = """{"deliveryId":"123"}"""
        )

        val notification = createNotification
            .execute(command)
            .subscribe()
            .withSubscriber(
                UniAssertSubscriber.create()
            )
            .awaitItem()
            .item

        assertNotNull(notification.id)
        assertEquals(eventId, notification.eventId)
        assertEquals("delivery.created", notification.eventType)
        assertEquals("user@example.com", notification.recipient)
        assertEquals(NotificationChannel.EMAIL, notification.channel)
        assertEquals(
            """{"deliveryId":"123"}""",
            notification.payload
        )
        assertEquals(NotificationStatus.PENDING, notification.status)
        assertEquals(0, notification.attempts)
        assertEquals(null, notification.lastError)
        assertEquals(null, notification.sentAt)
    }
}