package com.pl.platform.svc

import io.vertx.core.cli.annotations.Hidden
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.extensions.Extension

@Path("/")
class RootResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "notification-svc"
}