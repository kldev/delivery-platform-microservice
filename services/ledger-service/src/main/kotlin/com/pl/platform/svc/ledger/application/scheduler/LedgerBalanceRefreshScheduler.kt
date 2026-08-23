package com.pl.platform.svc.ledger.application.scheduler
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class LedgerBalanceRefreshScheduler(
    private val jdbcTemplate: JdbcTemplate,
) {

    @Scheduled(
        fixedDelayString = "\${ledger.balance.refresh-interval-ms:30000}",
    )
        fun refresh() {
        logger.debug("Refreshing ledger account balances")
        jdbcTemplate.execute("""
            CALL rebuild_ledger_account_balances()
            """.trimIndent())
    }

    companion object {
        private val logger =
            LoggerFactory.getLogger(
                LedgerBalanceRefreshScheduler::class.java,
            )
    }
}