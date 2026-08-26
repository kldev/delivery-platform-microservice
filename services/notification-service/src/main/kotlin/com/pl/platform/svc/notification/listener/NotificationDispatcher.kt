package com.pl.platform.svc.notification.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.pl.platform.common.messaging.event.EventMetadata
import com.pl.platform.svc.messaging.adapter.persistence.PgProcessedEventRepository
import com.pl.platform.svc.notification.service.NotificationService
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import jakarta.enterprise.context.ApplicationScoped
import java.time.Duration

@ApplicationScoped
class NotificationDispatcher(
    private val objectMapper: ObjectMapper,
    private val pool: Pool,
    private val processedEventRepository: PgProcessedEventRepository,
    private val notificationService: NotificationService
) {

    companion object {
        private val PROCESSING_TIMEOUT = Duration.ofSeconds(30)
    }

    fun dispatch(json: String): Uni<Void> {
        return Uni.createFrom()
            .item {
                objectMapper.readValue(json, EventMetadata::class.java)
            }
            .invoke { metadata ->
                Log.infof(
                    "Received event: eventId=%s, eventType=%s",
                    metadata.eventId,
                    metadata.eventType
                )
            }
            .onItem()
            .transformToUni { metadata ->
                process(metadata, json)
            }
            .ifNoItem()
            .after(PROCESSING_TIMEOUT)
            .fail()
            .onFailure()
            .invoke { error ->
                Log.error(
                    "Failed to process notification event",
                    error
                )
            }
    }

    private fun process(
        metadata: EventMetadata,
        json: String
    ): Uni<Void> =
        pool.withTransaction { connection ->

            processedEventRepository
                .save(connection, metadata)
                .onItem()
                .transformToUni { processed ->

                    if (!processed) {
                        Log.infof(
                            "Event already processed: eventId=%s, eventType=%s",
                            metadata.eventId,
                            metadata.eventType
                        )

                        return@transformToUni Uni.createFrom().voidItem()
                    }

                    notificationService.handle(
                        connection = connection,
                        event = metadata,
                        json = json
                    )
                }
        }
}