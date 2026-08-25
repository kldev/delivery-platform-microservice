package com.pl.platform.svc

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.vertx.mutiny.sqlclient.Pool
import jakarta.inject.Inject
import org.junit.jupiter.api.TestInstance

@QuarkusTest
@QuarkusTestResource(PostgresTestResource::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseIntegrationTest {

    @Inject
    lateinit var pool: Pool

    protected fun cleanDatabase() {
        pool
            .query(
                """
                TRUNCATE TABLE
                    notifications,
                    processed_events
                CASCADE
                """.trimIndent()
            )
            .execute()
            .await()
            .indefinitely()
    }
}