package com.pl.platform.svc.notification.scheduler

import com.pl.platform.svc.notification.adapter.persistence.NotificationProcessingRepository
import com.pl.platform.svc.notification.domain.Notification
import com.pl.platform.svc.notification.domain.NotificationStatus
import com.pl.platform.svc.notification.sender.NotificationRecipient
import com.pl.platform.svc.notification.sender.NotificationSenderRegistry
import io.quarkus.logging.Log
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class NotificationProcessor(
    private val repository: NotificationProcessingRepository,
    private val resolver: NotificationRecipientResolver,
    private val senders: NotificationSenderRegistry,
) {

    fun process(
        status: NotificationStatus,
        batchSize: Int,
    ): Uni<Void> =
        repository.claimBatch(
            status = status,
            batchSize = batchSize,
        )
            .onItem()
            .transformToMulti { notifications ->
                Multi.createFrom().iterable(notifications)
            }
            .onItem()
            // transformToUniAndConcatenate one by one
            // transformToUniAndMerge  async
            .transformToUniAndMerge { notification ->
                processNotification(notification)
            }
            .collect()
            .asList()
            .replaceWithVoid()

    private fun processNotification(
        notification: Notification,
    ): Uni<Void> =
        resolver.resolve(notification)
            .onItem()
            .transformToUni { recipient ->
                if (recipient == null) {
                    handleMissingRecipient(notification)
                } else {
                    send(notification, recipient)
                }
            }
            .onFailure()
            .recoverWithUni { error ->
                handleProcessingError(notification, error)
            }

    private fun send(
        notification: Notification,
        recipient: NotificationRecipient,
    ): Uni<Void> {
        val sender = senders.get(notification.channel) ?: return handleUnsupportedChannel(notification);

        return sender.send(
            notification = notification,
            recipient = recipient,
        )
            .onItem()
            .transformToUni {
                repository.markSent(notification.id.value)
            }
    }

    private fun handleMissingRecipient(
        notification: Notification,
    ): Uni<Void> =
        repository.markFailed(
            notification.id.value,
            "Recipient could not be resolved",
        )

    private fun handleUnsupportedChannel(
        notification: Notification,
    ): Uni<Void> =
        repository.markFailed(
            notification.id.value,
            "Unsupported notification channel: ${notification.channel}",
        )

    private fun handleProcessingError(
        notification: Notification,
        error: Throwable,
    ): Uni<Void> {
        Log.errorf(
            error,
            "Failed to process notification %s",
            notification.id,
        )

        return repository.markFailed(
            notification.id.value,
            error.message ?: error.javaClass.simpleName,
        )
    }
}