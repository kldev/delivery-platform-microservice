package com.pl.platform.svc.notification.query

import com.pl.platform.svc.notification.domain.Notification
import io.smallrye.mutiny.Uni

interface NotificationQueryRepository {

    fun find(
        query: NotificationQuery
    ): Uni<List<Notification>>
}