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
        val start = System.nanoTime()

        return Uni.createFrom().item(json)
            .onItem().transform { i ->
                objectMapper.readValue(i, EventMetadata::class.java)
            }
            .invoke { metadata ->
                Log.infof(
                    "START event: eventId=%s, eventType=%s",
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
            .invoke(Runnable  {
                val elapsed = Duration.ofNanos(
                    System.nanoTime() - start
                )

                Log.infof(
                    "END event processing: duration=%s",
                    elapsed
                )
            })
            .onFailure()
            .invoke { error ->
                val elapsed = Duration.ofNanos(
                    System.nanoTime() - start
                )

                Log.errorf(
                    error,
                    "FAILED event processing after %s",
                    elapsed
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