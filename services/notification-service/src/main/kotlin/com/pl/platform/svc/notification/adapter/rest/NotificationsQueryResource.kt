package com.pl.platform.svc.notification.adapter.rest

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.notification.adapter.rest.model.NotificationResponse
import com.pl.platform.svc.notification.query.NotificationQuery
import com.pl.platform.svc.notification.query.NotificationQueryRepository
import com.pl.platform.svc.notification.domain.NotificationStatus
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
class NotificationsQueryResource(
    private val repository: NotificationQueryRepository
) {

    @GET
    fun find(
        @QueryParam("status")
        status: NotificationStatus?,

        @QueryParam("eventId")
        eventId: UUID?,

        @QueryParam("from")
        from: LocalDate?,

        @QueryParam("to")
        to: LocalDate?,

        @QueryParam("timezone")
        timezone: String?,

        @QueryParam("offset")
        offset: Int?,

        @QueryParam("limit")
        limit: Int?
    ): Uni<SliceResponse<NotificationResponse>> {

        val query = NotificationQuery(
            status = status,
            eventId = eventId,
            from = from,
            to = to,
            timezone = timezone
                ?.let(ZoneId::of)
                ?: ZoneId.of("Europe/Warsaw"),
            offset = offset ?: 0,
            limit = limit ?: 100
        )

        return repository
            .find(query)
            .map { notifications ->
               val items = notifications.content.map(NotificationResponse::from)
                SliceResponse(
                    items,
                    notifications.hasNext
                )
            }
    }
}