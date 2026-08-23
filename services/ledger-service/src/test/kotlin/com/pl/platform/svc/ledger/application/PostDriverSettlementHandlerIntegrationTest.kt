package com.pl.platform.svc.ledger.application

import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.ledger.adapter.persistence.LedgerAccountJpaEntity
import com.pl.platform.svc.ledger.adapter.persistence.SpringDataLedgerAccountBalanceRepository
import com.pl.platform.svc.ledger.adapter.persistence.SpringDataLedgerAccountRepository
import com.pl.platform.svc.ledger.adapter.persistence.SpringDataLedgerTransactionRepository
import com.pl.platform.svc.ledger.application.use_case.PostDriverSettlementCommand
import com.pl.platform.svc.ledger.application.use_case.PostDriverSettlementHandler
import com.pl.platform.svc.ledger.domain.LedgerAccountOwnerType
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PostDriverSettlementHandlerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var handler: PostDriverSettlementHandler

    @Autowired
    private lateinit var ledgerAccountRepository: SpringDataLedgerAccountRepository

    @Autowired
    private lateinit var ledgerTransactionRepository: SpringDataLedgerTransactionRepository

    @Autowired
    private lateinit var ledgerAccountBalanceRepository: SpringDataLedgerAccountBalanceRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {
        cleanDatabase()
    }

    @Transactional
    private fun cleanDatabase() {
        jdbcTemplate.execute(
            """
        TRUNCATE TABLE
            ledger_entries,
            ledger_transactions,
            ledger_account_balances,
            ledger_accounts
        CASCADE
        """.trimIndent())

    }

    @Test
    fun `should create driver account and post settlement`() {
        val driverId = UUID.randomUUID()
        val settlementId = UUID.randomUUID()

        val platformAccount = createPlatformAccount()

        val command = command(
            settlementId = settlementId,
            driverId = driverId,
            amount = BigDecimal("125.50"),
            currency = "PLN"
        )

        handler.handle(command)

        val driverAccount =
            ledgerAccountRepository.findByOwnerTypeAndOwnerIdAndCurrency(
                ownerType = LedgerAccountOwnerType.DRIVER,
                ownerId = driverId,
                currency = "PLN",
            )

        assertNotNull(driverAccount)

        assertEquals(
            driverId,
            driverAccount.ownerId,
        )

        assertEquals(
            LedgerAccountOwnerType.DRIVER,
            driverAccount.ownerType,
        )

        assertEquals(
            "DRIVER:$driverId",
            driverAccount.name,
        )

        val transaction =
            ledgerTransactionRepository
                .findByReferenceTypeAndReferenceIdWithEntries(
                    referenceType = "SETTLEMENT",
                    referenceId = settlementId,
                )

        assertNotNull(transaction)

        assertEquals(
            2,
            transaction.entries.size,
        )

        val platformEntry =
            transaction.entries.first {
                it.accountId == platformAccount.id
            }

        val driverEntry =
            transaction.entries.first {
                it.accountId == driverAccount.id
            }

        assertAmount(
            BigDecimal("-125.50"),
            platformEntry.amount,
        )

        assertAmount(
            BigDecimal("125.50"),
            driverEntry.amount,
        )

        assertEquals(
            0,
            transaction.entries
                .sumOf { it.amount }
                .compareTo(BigDecimal.ZERO),
        )
    }

    @Test
    fun `should use existing driver account when posting settlement`() {
        val driverId = UUID.randomUUID()
        val settlementId = UUID.randomUUID()

        createPlatformAccount()

        val existingDriver =
            createDriverAccount(driverId)

        handler.handle(
            command(
                settlementId = settlementId,
                driverId = driverId,
                amount = BigDecimal("100.00"),

            )
        )

        val driverAccounts =
            ledgerAccountRepository.findAll()
                .filter {
                    it.ownerType == LedgerAccountOwnerType.DRIVER &&
                            it.ownerId == driverId &&
                            it.currency == "PLN"
                }

        assertEquals(1, driverAccounts.size)

        assertEquals(
            existingDriver.id,
            driverAccounts.single().id,
        )

        assertEquals(
            1,
            ledgerTransactionRepository.count(),
        )
    }

    @Test
    fun `should not create duplicate transaction when settlement is processed twice`() {
        val driverId = UUID.randomUUID()
        val settlementId = UUID.randomUUID()

        createPlatformAccount()

        val command =
            command(
                settlementId = settlementId,
                driverId = driverId,
                amount = BigDecimal("100.00"),
            )

        handler.handle(command)
        handler.handle(command)

        assertEquals(
            1,
            ledgerTransactionRepository.count(),
        )

        assertEquals(
            1,
            ledgerAccountRepository.findAll()
                .count {
                    it.ownerType == LedgerAccountOwnerType.DRIVER &&
                            it.ownerId == driverId
                },
        )

        val transaction =
            ledgerTransactionRepository
                .findByReferenceTypeAndReferenceIdWithEntries(
                    referenceType = "SETTLEMENT",
                    referenceId = settlementId,
                )

        assertNotNull(transaction)

        assertEquals(
            2,
            transaction.entries.size,
        )
    }

    @Test
    fun `should fail when platform account does not exist`() {
        val driverId = UUID.randomUUID()
        val settlementId = UUID.randomUUID()

        val exception =
            kotlin.runCatching {
                handler.handle(
                    command(
                        settlementId = settlementId,
                        driverId = driverId,
                        amount = BigDecimal("100.00"),
                        currency = "PLN"
                    )
                )
            }.exceptionOrNull()

        assertNotNull(exception)

        assertEquals(
            "Platform ledger account not found for currency PLN",
            exception.message,
        )

        assertEquals(
            0,
            ledgerAccountRepository.count(),
        )

        assertEquals(
            0,
            ledgerTransactionRepository.count(),
        )
    }

    @Test
    fun `should create separate transactions for different settlements`() {
        val driverId = UUID.randomUUID()

        createPlatformAccount()

        val firstSettlementId = UUID.randomUUID()
        val secondSettlementId = UUID.randomUUID()

        handler.handle(
            command(
                settlementId = firstSettlementId,
                driverId = driverId,
                amount = BigDecimal("100.00"),
            )
        )

        handler.handle(
            command(
                settlementId = secondSettlementId,
                driverId = driverId,
                amount = BigDecimal("150.00"),
            )
        )

        assertEquals(
            2,
            ledgerTransactionRepository.count(),
        )

        assertEquals(
            1,
            ledgerAccountRepository.findAll()
                .count {
                    it.ownerType == LedgerAccountOwnerType.DRIVER &&
                            it.ownerId == driverId
                },
        )

        val firstTransaction =
            ledgerTransactionRepository
                .findByReferenceTypeAndReferenceIdWithEntries(
                    "SETTLEMENT",
                    firstSettlementId,
                )

        val secondTransaction =
            ledgerTransactionRepository
                .findByReferenceTypeAndReferenceIdWithEntries(
                    "SETTLEMENT",
                    secondSettlementId,
                )

        assertNotNull(firstTransaction)
        assertNotNull(secondTransaction)

        assertEquals(
            0,
            firstTransaction.entries
                .sumOf { it.amount }
                .compareTo(BigDecimal.ZERO),
        )

        assertEquals(
            0,
            secondTransaction.entries
                .sumOf { it.amount }
                .compareTo(BigDecimal.ZERO),
        )
    }

    private fun createPlatformAccount(
        currency: String = "PLN",
    ) =
        ledgerAccountRepository.save(
            LedgerAccountJpaEntity(
                id = UUID.randomUUID(),
                name = "Platform $currency",
                ownerType = LedgerAccountOwnerType.PLATFORM,
                ownerId = null,
                currency = currency,
            )
        )

    private fun createDriverAccount(
        driverId: UUID,
        currency: String = "PLN",
    ) =
        ledgerAccountRepository.save(
            LedgerAccountJpaEntity(
                id = UUID.randomUUID(),
                name = "DRIVER:$driverId",
                ownerType = LedgerAccountOwnerType.DRIVER,
                ownerId = driverId,
                currency = currency,
            )
        )

    private fun command(
        settlementId: UUID,
        driverId: UUID,
        amount: BigDecimal,
        currency: String = "PLN",
    ) =
        PostDriverSettlementCommand(
            settlementId = settlementId,
            driverId = driverId,
            currency = currency,
            amount = amount,
            driverFullName = "DRIVER:$driverId",
            occurredAt = Instant.parse("2026-08-23T10:00:00Z"),
        )

    private fun assertAmount(
        expected: BigDecimal,
        actual: BigDecimal,
    ) {
        assertEquals(
            0,
            expected.compareTo(actual),
            "Expected amount $expected but was $actual",
        )
    }
}