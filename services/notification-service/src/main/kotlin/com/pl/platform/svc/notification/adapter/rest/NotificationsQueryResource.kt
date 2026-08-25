package com.pl.platform.svc.notification.adapter.rest

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.notification.adapter.rest.model.NotificationResponse
import com.pl.platform.svc.notification.query.NotificationQuery
import com.pl.platform.svc.notification.query.NotificationQueryRepository
import com.pl.platform.svc.notification.domain.NotificationStatus
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
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
        @Parameter(required = false)
        status: NotificationStatus?,

        @QueryParam("eventId")
        @Parameter(required = false)
        eventId: UUID?,


        @QueryParam("from")
        @Parameter(required = false)
        from: LocalDate?,

        @QueryParam("to")
        @Parameter(required = false)
        to: LocalDate?,

        @QueryParam("timezone")
        @DefaultValue("Europe/Warsaw")
        @Parameter(required = false)
        timezone: String?,

        @QueryParam("offset")
        @DefaultValue("0")
        @Parameter(required = false)
        offset: Int?,

        @QueryParam("limit")
        @DefaultValue("100")
        @Parameter(required = false)
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