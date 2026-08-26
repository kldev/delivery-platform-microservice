package com.pl.platform.svc.idempotency.port

import com.pl.platform.svc.idempotency.domain.IdempotencyRecord
import java.util.UUID

interface IdempotencyRepository {

    fun findByKey(key: UUID): IdempotencyRecord?

    fun save(record: IdempotencyRecord): IdempotencyRecord
}