package com.pl.platform.svc

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.PostgreSQLContainer

class PostgresTestResource : QuarkusTestResourceLifecycleManager {

    private lateinit var postgres: PostgreSQLContainer<*>

    override fun start(): Map<String, String> {
        postgres = PostgreSQLContainer("postgres:17")
            .withDatabaseName("notifications")
            .withUsername("notifications")
            .withPassword("notifications")

        postgres.start()

        return mapOf(
            "quarkus.datasource.db-kind" to "postgresql",
            "quarkus.datasource.username" to postgres.username,
            "quarkus.datasource.password" to postgres.password,
            "quarkus.datasource.reactive.url" to postgres.jdbcUrl
                .replace("jdbc:postgresql://", "postgresql://"),
            "quarkus.datasource.jdbc.url" to postgres.jdbcUrl
        )
    }

    override fun stop() {
        postgres.stop()
    }
}