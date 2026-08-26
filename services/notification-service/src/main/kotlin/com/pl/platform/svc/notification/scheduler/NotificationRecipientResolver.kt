package com.pl.platform.svc.notification.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Caffeine
import com.pl.platform.svc.delivery.service.DeliveryDriverService
import com.pl.platform.svc.notification.adapter.persistence.NotificationProcessingRepository
import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.sender.NotificationRecipient
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import java.time.Duration
import java.util.UUID

@ApplicationScoped
class NotificationRecipientResolver(
    private val deliveryService: DeliveryDriverService,
    private val objectMapper: ObjectMapper,
    private val repository: NotificationProcessingRepository,
) {

    private val driverCache = Caffeine.newBuilder()
        .maximumSize(50)
        .expireAfterWrite(Duration.ofMinutes(10))
        .build<UUID, NotificationRecipient>()

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
            .transformToUni { driver ->

                if (driver == null) {
                    return@transformToUni Uni.createFrom().nullItem()
                }

                driverCache.getIfPresent(driver.id)?.let { cachedRecipient ->
                    return@transformToUni updateRecipient(
                        notification,
                        cachedRecipient,
                    )
                }

                val recipient = NotificationRecipient(
                    fullName = "${driver.firstName} ${driver.lastName}",
                    phoneNumber = driver.phoneNumber,
                    email = driver.email,
                )

                driverCache.put(
                    driver.id,
                    recipient,
                )

                updateRecipient(
                    notification,
                    recipient,
                )
            }
    }

    private fun updateRecipient(
        notification: Notification,
        recipient: NotificationRecipient,
    ): Uni<NotificationRecipient> {

        val recipientJson = objectMapper.writeValueAsString(recipient)

        return repository
            .updateRecipient(
                notification.id.value,
                recipientJson,
            )
            .replaceWith(recipient)
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