package com.pl.platform.svc.ledger.adapter.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "ledger_entries")
class LedgerEntryJpaEntity(

    @Id
    var id: UUID,

    @Column(name = "transaction_id", nullable = false)
    var transactionId: UUID,

    @Column(name = "account_id", nullable = false)
    var accountId: UUID,

    @Column(
        name = "amount",
        nullable = false,
        precision = 19,
        scale = 4,
    )
    var amount: BigDecimal,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,
)