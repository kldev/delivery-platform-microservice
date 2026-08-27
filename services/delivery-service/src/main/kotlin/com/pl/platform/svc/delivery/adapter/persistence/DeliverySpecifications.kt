package com.pl.platform.svc.delivery.adapter.persistence

import com.pl.platform.svc.delivery.application.query.GetDeliveryQuery
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import org.springframework.data.jpa.domain.Specification
import java.util.UUID

object DeliverySpecifications {

    fun buildSpecifications(
        query: GetDeliveryQuery,
    ): Specification<DeliveryJpaEntity> {
        return listOfNotNull(

            query.status
                ?.let(::statusEquals),

            query.deliveryId
                ?.let(::deliveryIdEquals),
            query.driverId
                ?.let(::driverIdEquals),
        ).fold(
            Specification { _, _, _ -> null }
        ) { result, specification ->
            result.and(specification)
        }
    }


    private fun statusEquals(
        status: DeliveryStatus?,
    ): Specification<DeliveryJpaEntity>? =
        status?.let {
            Specification { root, _, cb ->
                cb.equal(
                    root.get<DeliveryStatusJpa>("status"),
                    DeliveryStatusJpa.from(it),
                )
            }
        }

    private fun deliveryIdEquals(
        deliveryId: UUID,
    ): Specification<DeliveryJpaEntity> =
            Specification { root, _, cb ->
                cb.equal(
                    root.get<UUID>("id"),
                    deliveryId,
                )
            }


    private fun driverIdEquals(
        driverId: UUID,
    ): Specification<DeliveryJpaEntity> =
            Specification { root, _, cb ->
                cb.equal(
                    root.get<UUID>("driverId"),
                    driverId,
                )
            }

}