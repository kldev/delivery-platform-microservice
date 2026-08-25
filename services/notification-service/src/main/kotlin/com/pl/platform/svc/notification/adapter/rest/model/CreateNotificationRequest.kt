package com.pl.platform.svc.notification.adapter.rest.model


import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.application.command.CreateNotificationCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateNotificationRequest(
    @field:NotNull
    val eventId: UUID,

    @field:NotBlank
    @field:Size(max = 255)
    val eventType: String,

    @field:NotBlank
    @field:Size(max = 500)
    val recipient: String,

    @field:NotNull
    val channel: NotificationChannel,

    @field:NotBlank
    val payload: String
) {
    fun toCommand() =
        CreateNotificationCommand(
            eventId = eventId,
            eventType = eventType,
            recipient = recipient,
            channel = channel,
            payload = payload
        )
}