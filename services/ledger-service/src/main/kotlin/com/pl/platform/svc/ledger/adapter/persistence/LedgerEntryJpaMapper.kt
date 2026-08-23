package com.pl.platform.svc.ledger.adapter.persistence

import com.pl.platform.svc.ledger.domain.LedgerEntry

fun LedgerEntryJpaEntity.toDomain(): LedgerEntry =
    LedgerEntry(
        id = id,
        accountId = accountId,
        amount = amount,
        type = type
    )