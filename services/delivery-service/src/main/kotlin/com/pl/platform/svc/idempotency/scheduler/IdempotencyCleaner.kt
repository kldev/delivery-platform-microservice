package com.pl.platform.svc.idempotency.scheduler

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class IdempotencyCleaner(
    private val jdbcTemplate: JdbcTemplate
) {

    @Scheduled(cron = $$"${idempotency.cleaner.cron:0 0 */3 * * *}")
    fun cleanup() {

        jdbcTemplate.execute("""
            DELETE FROM public.idempotency_records
            WHERE created_at < NOW() - INTERVAL '2 days';
        """.trimIndent())
    }
}