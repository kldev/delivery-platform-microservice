package com.pl.platform.svc.notification.adapter.rest

import com.pl.platform.svc.notification.adapter.rest.model.CreateNotificationRequest
import com.pl.platform.svc.notification.adapter.rest.model.NotificationResponse
import com.pl.platform.svc.notification.application.command.CreateNotification
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import jakarta.validation.Valid
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestResponse


@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
class NotificationsCommandResource(
    private val handler: CreateNotification,
) {

    @POST
    fun create(
        @Valid
        request: CreateNotificationRequest
    ): Uni<RestResponse<NotificationResponse>> =

        handler
            .execute(request.toCommand())
            .onItem()
            .transform {
                RestResponse.status(
                    Response.Status.CREATED,
                    NotificationResponse.from(it)
                )
            }


}
