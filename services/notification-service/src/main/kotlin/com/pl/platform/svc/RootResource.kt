package com.pl.platform.svc

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/")
class RootResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "notification-svc"
}