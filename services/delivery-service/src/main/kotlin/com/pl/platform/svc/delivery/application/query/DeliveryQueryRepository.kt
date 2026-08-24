package com.pl.platform.svc.delivery.application.query
import com.pl.platform.svc.common.AbstractJpaSliceQueryRepository
import com.pl.platform.svc.delivery.adapter.persistence.DeliveryJpaEntity
import com.pl.platform.svc.delivery.adapter.persistence.DeliverySpecifications
import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryItemResponse
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class DeliveryQueryRepository(entityManager: EntityManager) : AbstractJpaSliceQueryRepository<
        DeliveryJpaEntity,
        GetDeliveryQuery,
        DeliveryItemResponse
        >(entityManager) {
    override fun entityType(): Class<DeliveryJpaEntity>  =
        DeliveryJpaEntity::class.java

    override fun specification(query: GetDeliveryQuery): Specification<DeliveryJpaEntity>
        = DeliverySpecifications.buildSpecifications(query)

    override fun from(entity: DeliveryJpaEntity): DeliveryItemResponse
    = DeliveryItemResponse.from(entity)
}