package com.pl.platform.svc.ledger.adapter.persistence

import com.pl.platform.svc.ledger.application.port.LedgerAccountRepository
import com.pl.platform.svc.ledger.domain.LedgerAccount
import com.pl.platform.svc.ledger.domain.LedgerAccountOwnerType
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class LedgerAccountPersistenceAdapter(
    private val repository: SpringDataLedgerAccountRepository,
) : LedgerAccountRepository {

    override fun findByOwnerAndCurrency(
        ownerType: LedgerAccountOwnerType,
        ownerId: UUID?,
        currency: String,
    ): LedgerAccount? =
        repository
            .findByOwnerTypeAndOwnerIdAndCurrency(
                ownerType = ownerType,
                ownerId = ownerId,
                currency = currency,
            )
            ?.toDomain()

    override fun save(
        account: LedgerAccount,
    ): LedgerAccount =
        repository
            .save(account.toJpaEntity())
            .toDomain()
}