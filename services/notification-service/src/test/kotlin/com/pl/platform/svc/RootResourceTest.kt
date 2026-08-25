package com.pl.platform.svc

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class RootResourceTest : BaseIntegrationTest() {

    @Test
    fun testHelloEndpoint() {
        given()
          .`when`().get("/")
          .then()
             .statusCode(200)
             .body(`is`("notification-svc"))
    }

}