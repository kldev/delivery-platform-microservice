package com.pl.platform.svc.driver

import com.pl.platform.common.rest.ApiValidationError
import com.pl.platform.svc.BaseRestIntegrationTest
import com.pl.platform.svc.driver.adapter.rest.request.CreateDriverRequest
import com.pl.platform.svc.driver.adapter.rest.response.DriverResponse
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.expectBody
import kotlin.test.Test

class DriverRestIntegrationTest : BaseRestIntegrationTest() {

    @Test
    fun `should create driver`() {

        val request = CreateDriverRequest(
            firstName = "John",
            lastName = "Connor",
            phoneNumber = "+48123123123"
        )

        restTestClient
            .post()
            .uri(url("/api/drivers"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<DriverResponse>()
    }

    @Test
    fun `should return validation error when empty firstname `() {

        val request = CreateDriverRequest(
            firstName = "",
            lastName = "Connor",
            phoneNumber = "+48123123123"
        )

        restTestClient
            .post()
            .uri(url("/api/drivers"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody<ApiValidationError>()

    }
}