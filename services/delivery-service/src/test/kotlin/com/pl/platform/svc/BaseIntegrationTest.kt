package com.pl.platform.svc

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest
@AutoConfigureRestTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    companion object {

        @ServiceConnection
        @JvmStatic
        protected val postgres =
            PostgreSQLContainer("postgres:17")
                .withDatabaseName("delivery")
                .withUsername("delivery")
                .withPassword("delivery")

        @BeforeAll
        @JvmStatic
        fun startContainer() {
            postgres.start()
        }
    }
}