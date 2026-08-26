package com.pl.platform.svc.idempotency.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IdempotencyJpaRepository :
    JpaRepository<IdempotencyJpaEntity, UUID> {

    fun findByIdempotencyKey(
        idempotencyKey: UUID
    ): IdempotencyJpaEntity?
}