package com.pl.platform.svc.delivery.adapter.persistence
import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.domain.DeliveryId
import com.pl.platform.svc.delivery.port.DeliveryRepository
import org.springframework.stereotype.Repository

@Repository
class DeliveryPersistenceAdapter(
    private val repository: SpringDataDeliveryRepository
) : DeliveryRepository {

    override fun findById(id: DeliveryId): Delivery? =
        repository.findById(id.value)
            .map(DeliveryJpaEntity::toDomain)
            .orElse(null)

    override fun create(delivery: Delivery) {
        repository.save(
            DeliveryJpaEntity.create(delivery)
        )
    }

    override fun update(delivery: Delivery) {
        val entity = repository.findById(delivery.id.value)
            .orElseThrow {
                IllegalStateException(
                    "Delivery ${delivery.id.value} not found"
                )
            }

        entity.updateFrom(delivery)

        repository.save(entity)
    }

    override fun getAll(): List<Delivery> {
        return repository.findAll().map(DeliveryJpaEntity::toDomain)
    }
}