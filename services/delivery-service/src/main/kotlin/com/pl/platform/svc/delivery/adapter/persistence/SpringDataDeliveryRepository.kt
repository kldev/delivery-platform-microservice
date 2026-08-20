package com.pl.platform.svc.delivery.adapter.persistence

import com.pl.platform.svc.delivery.domain.Delivery
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataDeliveryRepository :
    JpaRepository<DeliveryJpaEntity, UUID>