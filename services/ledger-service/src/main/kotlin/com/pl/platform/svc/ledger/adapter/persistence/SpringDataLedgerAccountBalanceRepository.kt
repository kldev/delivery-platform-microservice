package com.pl.platform.svc.ledger.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataLedgerAccountBalanceRepository : JpaRepository<LedgerAccountBalanceJpaEntity, UUID>