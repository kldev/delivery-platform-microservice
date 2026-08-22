package com.pl.platform.svc.settlement.adapter.persistence

import com.pl.platform.svc.settlement.application.query.GetSettlementsListQuery
import org.springframework.data.jpa.domain.Specification
import java.util.UUID

object SettlementSpecifications {

    fun build(
        query: GetSettlementsListQuery,
    ): Specification<SettlementJpaEntity> {

        return listOfNotNull(
            query.search
                ?.takeIf { it.isNotBlank() }
                ?.let(::driverName),

            query.driverId
                ?.let(::driverId),

            query.deliveryId
                ?.let(::deliveryId),
        ).fold(
            Specification { _, _, _ -> null }
        ) { result, specification ->
            result.and(specification)
        }
    }

    fun driverId(
        driverId: UUID,
    ): Specification<SettlementJpaEntity> =
        Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(
                root.get<UUID>("driverId"),
                driverId,
            )
        }

    fun deliveryId(
        deliveryId: UUID,
    ): Specification<SettlementJpaEntity> =
        Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(
                root.get<UUID>("deliveryId"),
                deliveryId,
            )
        }

    fun driverName(
        search: String,
    ): Specification<SettlementJpaEntity> {
        val pattern = "%${search.trim().lowercase()}%"

        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.like(
                criteriaBuilder.lower(
                    root.get("driverFullName")
                ),
                pattern,
            )
        }
    }
}