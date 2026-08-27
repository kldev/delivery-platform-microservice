package com.pl.platform.svc.delivery.adapter.persistence

import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.domain.DeliveryId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "deliveries")
class DeliveryJpaEntity(

    @Id
    var id: UUID,

    @Column(name = "driver_id", nullable = false)
    var driverId: UUID?,

    @Column(name = "pickup_address", nullable = false, length = 500)
    var pickupAddress: String,

    @Column(name = "delivery_address", nullable = false, length = 500)
    var deliveryAddress: String,

    @Column(name = "price", nullable = false)
    var price: BigDecimal,

    @Column(name = "distance_km", nullable = false)
    var distanceKm: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: DeliveryStatusJpa,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,

    @Column(name = "currency", nullable = true)
    var currency: String?
) {

    fun toDomain(): Delivery =
        Delivery.reconstitute(
            id = DeliveryId(id),
            driverId = driverId,
            pickupAddress = pickupAddress,
            deliveryAddress = deliveryAddress,
            status = status.toDomain(),
            price = price,
            distanceKm = distanceKm,
            currency = currency,
        )

    fun updateFrom(delivery: Delivery) {
        driverId = delivery.driverId
        pickupAddress = delivery.pickupAddress
        deliveryAddress = delivery.deliveryAddress
        status = DeliveryStatusJpa.from(delivery.status)
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }

    companion object {

        fun create(delivery: Delivery): DeliveryJpaEntity {
            val now = Instant.now()

            return DeliveryJpaEntity(
                id = delivery.id.value,
                driverId = delivery.driverId,
                pickupAddress = delivery.pickupAddress,
                deliveryAddress = delivery.deliveryAddress,
                status = DeliveryStatusJpa.from(delivery.status),
                createdAt = now,
                updatedAt = now,
                price = delivery.price,
                distanceKm = delivery.distanceKm,
                currency = delivery.currency,
            )
        }
    }
}