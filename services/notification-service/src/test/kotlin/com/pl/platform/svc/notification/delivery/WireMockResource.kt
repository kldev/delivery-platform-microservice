package com.pl.platform.svc.notification.delivery

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

class WireMockResource : QuarkusTestResourceLifecycleManager {

    companion object {
        lateinit var wireMock: WireMockServer
            private set
    }

    override fun start(): Map<String, String> {
        wireMock = WireMockServer(
            WireMockConfiguration.options().dynamicPort()
        )

        wireMock.start()

        return mapOf(
            "quarkus.rest-client.delivery-client.url" to
                    "http://localhost:${wireMock.port()}"
        )
    }

    override fun stop() {
        wireMock.stop()
    }
}