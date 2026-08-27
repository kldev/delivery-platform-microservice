package com.pl.platform

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI


@Path("/")
class RootResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "Reporting Service"

    @GET
    @Path("/docs")
    fun docs(): Response = Response.temporaryRedirect(
        URI("/q/swagger-ui")).build()
}