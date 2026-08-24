package com.pl.platform.svc.delivery.adapter.persistence

import com.pl.platform.svc.delivery.application.query.GetDeliveryStatusDurationQuery
import org.springframework.data.jpa.domain.Specification
import java.util.*

object DeliveryStatusDurationSpecifications {

    fun build(
        query: GetDeliveryStatusDurationQuery,
    ): Specification<DeliveryStatusDurationJpaEntity> {
        return listOfNotNull(
            query.deliveryId
                ?.let(::deliveryIdEquals),
        ).fold(
            Specification { _, _, _ -> null }
        ) { result, specification ->
            result.and(specification)
        }
    }

    private fun deliveryIdEquals(
        deliveryId: UUID?,
    ): Specification<DeliveryStatusDurationJpaEntity>? =
        deliveryId?.let {
            Specification { root, _, cb ->
                cb.equal(
                    root.get<UUID>("deliveryId"),
                    it,
                )
            }
        }
}