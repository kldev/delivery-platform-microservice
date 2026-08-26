package com.pl.platform.svc.notification.scheduler

import com.pl.platform.svc.notification.domain.NotificationStatus
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class NotificationScheduler(
    private val processor: NotificationProcessor,
) {

    @Scheduled(every = "30s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)

    fun process(): Uni<Void> {
        Log.info("Processing notifications")

        return processor.process(
            status = NotificationStatus.PENDING,
            batchSize = 100,
        ).onFailure()
            .invoke { error ->
                Log.error("Failed to process notifications", error)
            }
    }
}
