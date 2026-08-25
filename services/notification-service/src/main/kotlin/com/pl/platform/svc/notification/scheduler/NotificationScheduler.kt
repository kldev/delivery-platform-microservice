package com.pl.platform.svc.notification.scheduler

import com.pl.platform.svc.notification.domain.NotificationStatus
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class NotificationScheduler(
    private val processor: NotificationProcessor,
) {

    @Scheduled(every = "5s")
    fun process() {
        processor.process(
            status = NotificationStatus.PENDING,
            batchSize = 100,
        )
    }
}