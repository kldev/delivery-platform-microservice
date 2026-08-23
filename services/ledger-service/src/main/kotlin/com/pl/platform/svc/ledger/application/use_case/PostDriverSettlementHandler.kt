package com.pl.platform.svc.ledger.application.use_case

import com.pl.platform.svc.ledger.application.port.LedgerAccountRepository
import com.pl.platform.svc.ledger.application.port.LedgerTransactionRepository
import com.pl.platform.svc.ledger.domain.LedgerAccountOwnerType
import com.pl.platform.svc.ledger.domain.LedgerTransaction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PostDriverSettlementHandler(
    private val ledgerAccountRepository: LedgerAccountRepository,
    private val ledgerTransactionRepository: LedgerTransactionRepository,
) {

    @Transactional
    fun handle(command: PostDriverSettlementCommand) {

        if (
            ledgerTransactionRepository.existsByReference(
                referenceType = "SETTLEMENT",
                referenceId = command.settlementId,
            )
        ) {
            return
        }

        val platformAccount =
            ledgerAccountRepository.findByOwnerAndCurrency(
                ownerType = LedgerAccountOwnerType.PLATFORM,
                ownerId = null,
                currency = command.currency,
            )
                ?: error(
                    "Platform ledger account not found for currency ${command.currency}"
                )

        val driverAccount =
            ledgerAccountRepository.findByOwnerAndCurrency(
                ownerType = LedgerAccountOwnerType.DRIVER,
                ownerId = command.driverId,
                currency = command.currency,
            )
                ?: ledgerAccountRepository.save(
                    com.pl.platform.svc.ledger.domain.LedgerAccount.driver(
                        id = UUID.randomUUID(),
                        name = command.driverFullName,
                        driverId = command.driverId,
                        currency = command.currency,
                    )
                )

        val transaction =
            LedgerTransaction.driverSettlement(
                id = UUID.randomUUID(),
                settlementId = command.settlementId,
                currency = command.currency,
                amount = command.amount,
                platformAccountId = platformAccount.id,
                driverAccountId = driverAccount.id,
                occurredAt = command.occurredAt,
            )

        ledgerTransactionRepository.save(transaction)
    }
}