package com.pl.platform.svc.ledger.application.port

import com.pl.platform.svc.ledger.domain.LedgerTransaction

import java.util.UUID

interface LedgerTransactionRepository {

    fun existsByReference(
        referenceType: String,
        referenceId: UUID,
    ): Boolean

    fun save(transaction: LedgerTransaction): LedgerTransaction
}