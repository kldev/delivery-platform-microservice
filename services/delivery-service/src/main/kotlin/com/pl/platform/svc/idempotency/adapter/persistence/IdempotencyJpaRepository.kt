package com.pl.platform.svc.idempotency.adapter.persistence

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface IdempotencyJpaRepository :
    JpaRepository<IdempotencyJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select idem
        from IdempotencyJpaEntity idem
        where idem.idempotencyKey = :key
    """)
    fun findByIdempotencyKey(
        key: UUID
    ): IdempotencyJpaEntity?
}