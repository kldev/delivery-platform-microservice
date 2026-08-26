package com.pl.platform.svc.notification.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import com.pl.platform.svc.delivery.service.DeliveryDriverService
import com.pl.platform.svc.notification.adapter.persistence.NotificationProcessingRepository
import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.query.NotificationQueryRepository
import com.pl.platform.svc.notification.sender.NotificationRecipient
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class NotificationRecipientResolver(
    private val deliveryService: DeliveryDriverService,
    private val objectMapper: ObjectMapper,
    private val repository: NotificationProcessingRepository,
) {

    fun resolve(
        notification: Notification,
    ): Uni<NotificationRecipient?> {

        if (notification.recipient.isNotBlank()) {
            return parseRecipient(notification.recipient)
        }

        val deliveryId = extractDeliveryId(notification.payload)
            ?: return Uni.createFrom().nullItem()

        return deliveryService
            .findDriverByDeliveryId(deliveryId)
            .onItem()
            .transformToUni { delivery ->

                if (delivery == null) {
                    return@transformToUni Uni.createFrom().nullItem()
                }

                val recipient = NotificationRecipient(
                    fullName = "${delivery.firstName} ${delivery.lastName}",
                    phoneNumber = delivery.phoneNumber,
                    email = delivery.email,
                )

                val recipientJson = objectMapper.writeValueAsString(recipient)

                repository
                    .updateRecipient(
                        notification.id.value,
                        recipientJson,
                    )
                    .replaceWith(recipient)
            }
    }

    private fun parseRecipient(
        recipient: String,
    ): Uni<NotificationRecipient?> =
        Uni.createFrom().item {
            objectMapper.readValue(
                recipient,
                NotificationRecipient::class.java,
            )
        }

    private fun extractDeliveryId(
        payload: String,
    ): UUID? =
        runCatching {
            objectMapper
                .readTree(payload)
                .get("deliveryId")
                ?.takeIf { !it.isNull }
                ?.asText()
                ?.let(UUID::fromString)
        }.getOrNull()
}