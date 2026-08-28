package com.pl.platform.svc.notification.listener

import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message

@ApplicationScoped
@IfBuildProperty(
    name = "notifications.consumer.enabled",
    stringValue = "true"
)
class NotificationConsumer(
    private val dispatcher: NotificationDispatcher
) {

    @Incoming("notifications")
    fun consume(
        message: Message<String>
    ): Uni<Void> {


        return dispatcher
            .dispatch(message.payload)
            .invoke(Runnable{
                message.ack()
            })
            .onFailure()
            .invoke { error ->
                Log.errorf(
                    error,
                    "FAILED NotificationConsumer : %s",
                    message.payload
                )
            }
    }
}