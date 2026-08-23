package com.pl.platform.svc.ledger.application.port

import com.pl.platform.svc.ledger.domain.LedgerAccount
import com.pl.platform.svc.ledger.domain.LedgerAccountOwnerType
import java.util.UUID

interface LedgerAccountRepository {

    fun findByOwnerAndCurrency(
        ownerType: LedgerAccountOwnerType,
        ownerId: UUID?,
        currency: String,
    ): LedgerAccount?

    fun save(account: LedgerAccount): LedgerAccount

    fun getAll(): List<LedgerAccount>
    fun getById(id: UUID): LedgerAccount?
}