package com.pl.platform.svc.notification.query

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.notification.domain.Notification
import io.smallrye.mutiny.Uni

interface NotificationQueryRepository {

    fun find(
        query: NotificationQuery
    ): Uni<SliceResponse<Notification>>
}