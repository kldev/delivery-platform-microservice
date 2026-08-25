package com.pl.platform.svc.delivery.client

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.delivery.client.model.DeliveryItemResponse
import com.pl.platform.svc.delivery.client.model.DeliveryStatus
import com.pl.platform.svc.delivery.client.model.DriverResponse
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import java.util.UUID

@RegisterRestClient(configKey = "delivery-client")
@Path("/api")
interface DeliveryClient {

    @GET
    @Path("/deliveries")
    fun getDeliveries(
        @QueryParam("status")
        status: DeliveryStatus? = null,

        @QueryParam("deliveryId")
        deliveryId: UUID? = null,
    ): SliceResponse<DeliveryItemResponse>

    @GET
    @Path("/drivers")
    fun getDrivers(
        @QueryParam("driverId")
        driverId: UUID? = null,
    ): List<DriverResponse>
}