package com.pl.platform.svc.ledger.adapter.persistence

import com.pl.platform.svc.ledger.domain.LedgerTransaction

fun LedgerTransactionJpaEntity.toDomain(): LedgerTransaction =
    LedgerTransaction.create(
        id = id,
        type = type,
        referenceType = referenceType,
        referenceId = referenceId,
        currency = currency,
        occurredAt = occurredAt,
        entries = entries.map { it.toDomain() },
    )

fun LedgerTransaction.toJpaEntity(): LedgerTransactionJpaEntity {
    val entity = LedgerTransactionJpaEntity(
        id = id,
        type = type,
        referenceType = referenceType,
        referenceId = referenceId,
        currency = currency,
        occurredAt = occurredAt,
    )

    entity.entries.addAll(
        entries.map {
            LedgerEntryJpaEntity(
                id = it.id,
                transactionId = id,
                accountId = it.accountId,
                amount = it.amount,
            )
        }
    )

    return entity
}