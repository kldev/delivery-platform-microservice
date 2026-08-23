package com.pl.platform.svc.ledger.adapter.persistence

import com.pl.platform.svc.ledger.domain.LedgerAccountOwnerType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataLedgerAccountRepository :
    JpaRepository<LedgerAccountJpaEntity, UUID> {

    fun findByOwnerTypeAndOwnerIdAndCurrency(
        ownerType: LedgerAccountOwnerType,
        ownerId: UUID?,
        currency: String,
    ): LedgerAccountJpaEntity?

    fun findByOwnerTypeAndCurrency(
        ownerType: LedgerAccountOwnerType,
        currency: String,
    ): LedgerAccountJpaEntity?
}