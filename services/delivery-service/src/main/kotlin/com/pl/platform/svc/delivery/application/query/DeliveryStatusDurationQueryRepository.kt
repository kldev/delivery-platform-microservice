package com.pl.platform.svc.delivery.application.query

import com.pl.platform.svc.common.AbstractJpaSliceQueryRepository
import com.pl.platform.svc.delivery.adapter.persistence.DeliveryStatusDurationJpaEntity
import com.pl.platform.svc.delivery.adapter.persistence.DeliveryStatusDurationSpecifications
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class DeliveryStatusDurationQueryRepository(entityManager: EntityManager)
    : AbstractJpaSliceQueryRepository<DeliveryStatusDurationJpaEntity,
        GetDeliveryStatusDurationQuery,
DeliveryStatusDurationItem>(entityManager) {
    override fun entityType(): Class<DeliveryStatusDurationJpaEntity> = DeliveryStatusDurationJpaEntity::class.java

    override fun specification(query: GetDeliveryStatusDurationQuery):
            Specification<DeliveryStatusDurationJpaEntity> = DeliveryStatusDurationSpecifications.build(query)


    override fun from(entity: DeliveryStatusDurationJpaEntity): DeliveryStatusDurationItem =
        DeliveryStatusDurationItem.from(entity)

}