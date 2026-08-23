package com.pl.platform.svc.ledger.adapter.persistence

import com.pl.platform.svc.ledger.domain.LedgerTransactionType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "ledger_transactions",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_ledger_transactions_reference",
            columnNames = ["reference_type", "reference_id"],
        ),
    ],
)
class LedgerTransactionJpaEntity(

    @Id
    val id: UUID,

    @Column(name = "type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    val type: LedgerTransactionType,

    @Column(name = "reference_type", nullable = false, length = 50)
    val referenceType: String,

    @Column(name = "reference_id", nullable = false)
    val referenceId: UUID,

    @Column(name = "currency", nullable = false, length = 3)
    val currency: String,

    @Column(name = "occurred_at", nullable = false)
    val occurredAt: Instant,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    @JoinColumn(name = "transaction_id")
    val entries: MutableList<LedgerEntryJpaEntity> = mutableListOf()
)