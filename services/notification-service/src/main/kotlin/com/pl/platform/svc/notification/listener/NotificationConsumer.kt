package com.pl.platform.svc.notification.listener

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message

@ApplicationScoped
class NotificationConsumer(
    private val dispatcher: NotificationDispatcher
) {

    @Incoming("notifications")
    fun consume(
        message: Message<String>
    ): Uni<Void> {

        return dispatcher
            .dispatch(message.payload)

//            .onItem()
//            .transformToUni { _ ->
//                Uni.createFrom()
//                    .completionStage(message.ack())
//            }
    }
}