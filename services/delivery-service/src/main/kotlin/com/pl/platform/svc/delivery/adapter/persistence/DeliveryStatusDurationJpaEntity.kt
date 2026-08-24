package com.pl.platform.svc.delivery.adapter.persistence

import com.pl.platform.svc.delivery.domain.DeliveryStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "delivery_status_duration")
class DeliveryStatusDurationJpaEntity(
    @Id
    @Column(name = "id")
    var id: UUID,

    @Column(name = "delivery_id")
    var deliveryId: UUID,

    @Column(name = "previous_status")
    @Enumerated(EnumType.STRING)
    var previousStatus: DeliveryStatus? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: DeliveryStatus,

    @Column(name = "status_from")
    var statusFrom: Instant? = null,

    @Column(name = "status_to")
    var statusTo: Instant? = null,

    @Column(name = "duration_seconds")
    var durationSeconds: BigDecimal
)