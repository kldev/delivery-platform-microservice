package com.pl.platform.svc.notification.service

import com.pl.platform.common.messaging.event.Event
import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationChannel
import com.pl.platform.svc.notification.port.NotificationRepository
import io.quarkus.logging.Log
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.SqlConnection
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class NotificationService(
    private val policy: NotificationPolicy,
    private val repository: NotificationRepository,
) {

    fun handle(
        connection: SqlConnection,
        event: Event,
        json: String
    ): Uni<Void> {

        val channels =
            policy.channelsFor(event)

        if (channels.isEmpty()) {
            Log.info("No channels found for event: ${event.eventType}")
            return Uni.createFrom().voidItem()
        }

        Log.info("Create channels entry for event: ${event.eventId} ${event.eventType} $channels")

        val operations: List<Uni<Notification>> =
            channels.map { channel ->

                val notification =
                    createNotification(
                        metadata = event,
                        json = json,
                        channel = channel
                    )

                repository
                    .create(connection, notification)
                    .onItem()
                    .transform { current ->
                        requireNotNull(current) {
                            "Notification not created: " +
                                    "eventId=${event.eventId}, " +
                                    "eventType=${event.eventType}, " +
                                    "channel=$channel"
                        }
                    }
            }

        return Multi.createFrom()
            .iterable(operations)
            .onItem()
            .transformToUniAndConcatenate { it }
            .collect()
            .asList()
            .replaceWithVoid()
    }


    private fun createNotification(
        metadata: Event,
        json: String,
        channel: NotificationChannel
    ): Notification {

        return Notification.create(
            metadata,
            channel = channel,
            payload = json
        )
    }
}