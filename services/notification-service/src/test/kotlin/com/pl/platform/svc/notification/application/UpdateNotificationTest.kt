package com.pl.platform.svc.notification.application

import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.notification.application.command.UpdateNotification
import com.pl.platform.svc.notification.application.command.UpdateNotificationCommand
import com.pl.platform.svc.notification.application.command.CreateNotification
import com.pl.platform.svc.notification.application.command.CreateNotificationCommand
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.domain.NotificationId
import com.pl.platform.svc.notification.port.NotificationRepository
import com.pl.platform.svc.notification.domain.NotificationStatus
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@QuarkusTest
class UpdateNotificationTest : BaseIntegrationTest() {

    @Inject
    lateinit var createNotification: CreateNotification

    @Inject
    lateinit var updateNotification: UpdateNotification

    @Inject
    lateinit var repository: NotificationRepository

    @Test
    fun `should update notification`() {
        val created = createNotification
            .execute(
                CreateNotificationCommand(
                    eventId = UUID.randomUUID(),
                    eventType = "DeliveryCreated",
                    recipient = "user@example.com",
                    channel = NotificationChannel.EMAIL,
                    payload = """{"deliveryId":"123"}"""
                )
            )
            .await()
            .indefinitely()

        val sentAt = Instant.now()
            .truncatedTo(ChronoUnit.MICROS)

        val updated = updateNotification
            .execute(
                UpdateNotificationCommand(
                    id = created.id,
                    status = NotificationStatus.SENT,
                    attempts = 1,
                    lastError = null,
                    sentAt = sentAt
                )
            )
            .await()
            .indefinitely()

        assertEquals(created.id, updated.id)
        assertEquals(NotificationStatus.SENT, updated.status)
        assertEquals(1, updated.attempts)
        assertEquals(sentAt, updated.sentAt)
        assertEquals(null, updated.lastError)

        val persisted = repository
            .findById(created.id)
            .await()
            .indefinitely()

        assertNotNull(persisted)
        assertEquals(NotificationStatus.SENT, persisted!!.status)
        assertEquals(1, persisted.attempts)
        assertEquals(sentAt, persisted.sentAt)
    }

    @Test
    fun `should fail when notification does not exist`() {
        val id = NotificationId(
            UUID.randomUUID()
        )

        val failure = updateNotification
            .execute(
                UpdateNotificationCommand(
                    id = id,
                    status = NotificationStatus.SENT,
                    attempts = 1
                )
            )
            .subscribe()
            .withSubscriber(
                UniAssertSubscriber.create()
            )
            .awaitFailure()
            .failure

        assertEquals(
            "Notification ${id.value} not found",
            failure.message
        )
    }
}