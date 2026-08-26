package com.pl.platform.svc.idempotency.adapter.persistence


import com.pl.platform.svc.idempotency.domain.IdempotencyStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID
@Entity
@Table(
    name = "idempotency_records",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_idempotency_records_key",
            columnNames = ["idempotency_key"]
        )
    ]
)
class IdempotencyJpaEntity(

    @Id
    val id: UUID,

    @Column(
        name = "idempotency_key",
        nullable = false,
        unique = true
    )
    val idempotencyKey: UUID,

    @Column(
        name = "request_hash",
        nullable = false,
        length = 64
    )
    val requestHash: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: IdempotencyStatus,

    @Column(name = "response_status")
    var responseStatus: Int? = null,

    @Column(name = "response_body", columnDefinition = "TEXT")
    var responseBody: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant,

    @Column(name = "completed_at")
    var completedAt: Instant? = null
)