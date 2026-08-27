package com.pl.platform.svc.notification.scheduler

import com.pl.platform.svc.notification.domain.NotificationStatus
import com.pl.platform.svc.notification.port.BackgroundRepository
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import java.util.function.Supplier


@ApplicationScoped
class NotificationScheduler(
    private val processor: NotificationProcessor,
    private val backgroundRepository: BackgroundRepository
) {
    @Scheduled(every = "{scheduler.notifications.interval}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)

    fun process(): Uni<Void> {
        val startedAt = System.nanoTime()

        Log.info("Processing notifications")

        return processor.process(
            status = NotificationStatus.PENDING,
            batchSize = 500,
        ) .call (Supplier {
            backgroundRepository.recordExecution(
                taskName = "notifications",
                durationMs = elapsedMs(startedAt)
            )
        })
            .onFailure()
            .call { error ->
                backgroundRepository.recordExecution(
                    taskName = "notifications",
                    durationMs = elapsedMs(startedAt),
                    error = error
                )
            }
            .onFailure()
            .invoke { error ->
                Log.error("Failed to process notifications", error)
            }
    }

    private fun elapsedMs(startedAt: Long): Long =
        (System.nanoTime() - startedAt) / 1_000_000
}
