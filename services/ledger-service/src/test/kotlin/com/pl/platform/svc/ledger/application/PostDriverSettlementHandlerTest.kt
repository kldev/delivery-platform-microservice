package com.pl.platform.svc.ledger.application

import com.pl.platform.svc.ledger.application.port.LedgerAccountRepository
import com.pl.platform.svc.ledger.application.port.LedgerTransactionRepository
import com.pl.platform.svc.ledger.application.use_case.PostDriverSettlementCommand
import com.pl.platform.svc.ledger.application.use_case.PostDriverSettlementHandler
import com.pl.platform.svc.ledger.domain.LedgerAccount
import com.pl.platform.svc.ledger.domain.LedgerAccountOwnerType
import com.pl.platform.svc.ledger.domain.LedgerTransaction
import com.pl.platform.svc.ledger.domain.LedgerTransactionType
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

class PostDriverSettlementHandlerTest {

    private val ledgerAccountRepository = mockk<LedgerAccountRepository>()
    private val ledgerTransactionRepository = mockk<LedgerTransactionRepository>()

    private val handler = PostDriverSettlementHandler(
        ledgerAccountRepository = ledgerAccountRepository,
        ledgerTransactionRepository = ledgerTransactionRepository,
    )

    @Test
    fun `should do nothing when settlement was already posted`() {
        val settlementId = UUID.randomUUID()

        val command = command(
            settlementId = settlementId,
        )

        every {
            ledgerTransactionRepository.existsByReference(
                referenceType = "SETTLEMENT",
                referenceId = settlementId,
            )
        } returns true

        handler.handle(command)

        verify(exactly = 0) {
            ledgerAccountRepository.findByOwnerAndCurrency(
                any(),
                any(),
                any(),
            )
        }

        verify(exactly = 0) {
            ledgerAccountRepository.save(any())
        }

        verify(exactly = 0) {
            ledgerTransactionRepository.save(any())
        }
    }

    @Test
    fun `should debit platform and credit driver when posting settlement`() {
        val settlementId = UUID.randomUUID()
        val driverId = UUID.randomUUID()

        val platformAccount = platformAccount()
        val driverAccount = driverAccount(
            driverId = driverId,
        )

        val command = command(
            settlementId = settlementId,
            driverId = driverId,
            amount = BigDecimal("125.50"),
        )

        every {
            ledgerTransactionRepository.existsByReference(
                "SETTLEMENT",
                settlementId,
            )
        } returns false

        every {
            ledgerAccountRepository.findByOwnerAndCurrency(
                LedgerAccountOwnerType.PLATFORM,
                null,
                command.currency,
            )
        } returns platformAccount

        every {
            ledgerAccountRepository.findByOwnerAndCurrency(
                LedgerAccountOwnerType.DRIVER,
                driverId,
                command.currency,
            )
        } returns driverAccount

        val transactionSlot = slot<LedgerTransaction>()

        every {
            ledgerTransactionRepository.save(capture(transactionSlot))
        } answers {
            firstArg()
        }

        handler.handle(command)

        val transaction = transactionSlot.captured

        assertEquals(
            LedgerTransactionType.DRIVER_SETTLEMENT,
            transaction.type,
        )

        assertEquals(
            settlementId,
            transaction.referenceId,
        )

        assertEquals(
            command.currency,
            transaction.currency,
        )

        assertEquals(
            BigDecimal("-125.50"),
            transaction.entries
                .first { it.accountId == platformAccount.id }
                .amount,
        )

        assertEquals(
            BigDecimal("125.50"),
            transaction.entries
                .first { it.accountId == driverAccount.id }
                .amount,
        )

        assertTrue(
            transaction.entries.sumOf { it.amount }.compareTo(BigDecimal.ZERO) == 0,
        )

        verify(exactly = 0) {
            ledgerAccountRepository.save(any())
        }

        verify(exactly = 1) {
            ledgerTransactionRepository.save(any())
        }
    }

    @Test
    fun `should create driver account when it does not exist`() {
        val settlementId = UUID.randomUUID()
        val driverId = UUID.randomUUID()

        val platformAccount = platformAccount()

        val command = command(
            settlementId = settlementId,
            driverId = driverId,
            amount = BigDecimal("100.00"),
        )

        every {
            ledgerTransactionRepository.existsByReference(
                "SETTLEMENT",
                settlementId,
            )
        } returns false

        every {
            ledgerAccountRepository.findByOwnerAndCurrency(
                LedgerAccountOwnerType.PLATFORM,
                null,
                command.currency,
            )
        } returns platformAccount

        every {
            ledgerAccountRepository.findByOwnerAndCurrency(
                LedgerAccountOwnerType.DRIVER,
                driverId,
                command.currency,
            )
        } returns null

        val accountSlot = slot<LedgerAccount>()

        every {
            ledgerAccountRepository.save(capture(accountSlot))
        } answers {
            firstArg()
        }

        val transactionSlot = slot<LedgerTransaction>()

        every {
            ledgerTransactionRepository.save(capture(transactionSlot))
        } answers {
            firstArg()
        }

        handler.handle(command)

        val createdAccount = accountSlot.captured

        assertEquals(
            LedgerAccountOwnerType.DRIVER,
            createdAccount.ownerType,
        )

        assertEquals(
            driverId,
            createdAccount.ownerId,
        )

        assertEquals(
            command.currency,
            createdAccount.currency,
        )

        assertEquals(
            "John Connor",
            createdAccount.name,
        )

        val transaction = transactionSlot.captured

        assertEquals(
            2,
            transaction.entries.size,
        )

        assertTrue(
            transaction.entries.sumOf { it.amount }.compareTo(BigDecimal.ZERO) == 0,
        )

        verify(exactly = 1) {
            ledgerAccountRepository.save(any())
        }

        verify(exactly = 1) {
            ledgerTransactionRepository.save(any())
        }
    }

    @Test
    fun `should fail when platform account does not exist`() {
        val settlementId = UUID.randomUUID()

        val command = command(
            settlementId = settlementId,
        )

        every {
            ledgerTransactionRepository.existsByReference(
                "SETTLEMENT",
                settlementId,
            )
        } returns false

        every {
            ledgerAccountRepository.findByOwnerAndCurrency(
                LedgerAccountOwnerType.PLATFORM,
                null,
                command.currency,
            )
        } returns null

        assertThrows<IllegalStateException> {
            handler.handle(command)
        }

        verify(exactly = 0) {
            ledgerAccountRepository.save(any())
        }

        verify(exactly = 0) {
            ledgerTransactionRepository.save(any())
        }
    }

    @Test
    fun `should create balanced transaction with platform debit and driver credit`() {
        val driverId = UUID.randomUUID()

        val platformAccount = platformAccount()
        val driverAccount = driverAccount(driverId)

        val command = command(
            driverId = driverId,
            amount = BigDecimal("250.75"),
        )

        every {
            ledgerTransactionRepository.existsByReference(any(), any())
        } returns false

        every {
            ledgerAccountRepository.findByOwnerAndCurrency(
                LedgerAccountOwnerType.PLATFORM,
                null,
                command.currency,
            )
        } returns platformAccount

        every {
            ledgerAccountRepository.findByOwnerAndCurrency(
                LedgerAccountOwnerType.DRIVER,
                driverId,
                command.currency,
            )
        } returns driverAccount

        val transactionSlot = slot<LedgerTransaction>()

        every {
            ledgerTransactionRepository.save(capture(transactionSlot))
        } answers {
            firstArg()
        }

        handler.handle(command)

        val transaction = transactionSlot.captured

        assertEquals(2, transaction.entries.size)

        val platformEntry =
            transaction.entries.first {
                it.accountId == platformAccount.id
            }

        val driverEntry =
            transaction.entries.first {
                it.accountId == driverAccount.id
            }

        assertEquals(
            BigDecimal("-250.75"),
            platformEntry.amount,
        )

        assertEquals(
            BigDecimal("250.75"),
            driverEntry.amount,
        )

        assertTrue(
            transaction.entries.sumOf { it.amount }.compareTo(BigDecimal.ZERO) == 0,
        )
    }

    private fun command(
        settlementId: UUID = UUID.randomUUID(),
        driverId: UUID = UUID.randomUUID(),
        currency: String = "PLN",
        amount: BigDecimal = BigDecimal("100.00"),
    ) = PostDriverSettlementCommand(
        settlementId = settlementId,
        driverId = driverId,
        currency = currency,
        amount = amount,
        driverFullName = "John Connor",
        occurredAt = Instant.parse("2026-08-23T10:00:00Z"),
    )

    private fun platformAccount(
        currency: String = "PLN",
    ): LedgerAccount =
        LedgerAccount.platform(
            id = UUID.randomUUID(),
            name = "Platform PLN",
            currency = currency,
        )

    private fun driverAccount(
        driverId: UUID,
        currency: String = "PLN",
    ): LedgerAccount =
        LedgerAccount.driver(
            id = UUID.randomUUID(),
            name = "DRIVER:$driverId",
            driverId = driverId,
            currency = currency,
        )
}