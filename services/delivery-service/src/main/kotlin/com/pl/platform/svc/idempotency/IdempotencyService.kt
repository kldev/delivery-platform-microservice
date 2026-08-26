package com.pl.platform.svc.idempotency

import com.pl.platform.svc.idempotency.adapter.IdempotencyKeyReuseException
import com.pl.platform.svc.idempotency.adapter.IdempotencyProperties
import com.pl.platform.svc.idempotency.adapter.persistence.IdempotencyJpaEntity
import com.pl.platform.svc.idempotency.adapter.persistence.IdempotencyJpaRepository
import com.pl.platform.svc.idempotency.domain.IdempotencyStatus
import com.pl.platform.svc.idempotency.exception.IdempotencyRequestInProgressException
import com.pl.platform.svc.idempotency.model.IdempotencyCheckResult
import com.pl.platform.svc.idempotency.util.RequestHasher
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Clock
import java.time.Instant
import java.util.*

@Service
class IdempotencyService(
    private val repository: IdempotencyJpaRepository,
    private val hasher: RequestHasher,
    private val properties: IdempotencyProperties,
    private val clock: Clock
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun check(
        key: UUID,
        body: ByteArray
    ): IdempotencyCheckResult {

        val now = Instant.now(clock)
        val hash = hasher.hash(body)

        val existing = repository.findByIdempotencyKey(key)

        if (existing != null) {

            if (existing.expiresAt.isBefore(now)) {
                repository.delete(existing)
                repository.flush()
            } else {
                if (existing.requestHash != hash) {
                    throw IdempotencyKeyReuseException()
                }

                return when (existing.status) {

                    IdempotencyStatus.IN_PROGRESS ->
                        throw IdempotencyRequestInProgressException()

                    IdempotencyStatus.COMPLETED ->
                        IdempotencyCheckResult.Replay(
                            status = requireNotNull(existing.responseStatus),
                            body = requireNotNull(existing.responseBody)
                        )
                }
            }
        }

        try {
            repository.saveAndFlush(
                IdempotencyJpaEntity(
                    id = UUID.randomUUID(),
                    idempotencyKey = key,
                    requestHash = hash,
                    status = IdempotencyStatus.IN_PROGRESS,
                    createdAt = Instant.now(),
                    expiresAt = now.plus(properties.timeout)
                )
            )
        } catch (ex: DataIntegrityViolationException) {
            /*
             * Concurrent request with the same key.
             *
             * The UNIQUE constraint is the final protection.
             */
            throw IllegalStateException(
                "Concurrent request with the same Idempotency-Key",
                ex
            )
        }

        return IdempotencyCheckResult.New(
            requestHash = hash
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun complete(
        key: UUID,
        status: Int,
        responseBody: String
    ) {
        val record = repository.findByIdempotencyKey(key)
            ?: error("Idempotency record not found: $key")

        record.status = IdempotencyStatus.COMPLETED
        record.responseStatus = status
        record.responseBody = responseBody
        record.completedAt = Instant.now()

        repository.save(record)
    }

}