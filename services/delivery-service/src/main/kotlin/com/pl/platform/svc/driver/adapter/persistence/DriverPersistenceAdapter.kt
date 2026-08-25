package com.pl.platform.svc.driver.adapter.persistence

import com.pl.platform.svc.driver.domain.Driver
import com.pl.platform.svc.driver.domain.DriverId
import com.pl.platform.svc.driver.port.DriverRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class DriverPersistenceAdapter(
    private val repository: SpringDataDriverRepository) : DriverRepository  {
    override fun findById(id: DriverId): Driver? {
        return repository.findById(id.value)
            .map(DriverJpaEntity::toDomain)
            .orElse(null);
    }

    override fun findByPhoneNumber(phoneNumber: String): Driver? {
       return repository.findByPhoneNumber(phoneNumber)
           ?.toDomain();
    }

    override fun create(driver: Driver) {
        repository.save(DriverJpaEntity.create(driver))
            .toDomain();
    }

    override fun update(driver: Driver) {
        val entity: DriverJpaEntity = repository.findById(driver.id.value)
            .orElseThrow();
        entity.updateFrom(driver)
        repository.save(entity)
    }

    override fun existsByPhoneNumber(phoneNumber: String): Boolean {
        return repository.existsByPhoneNumber(phoneNumber)
    }

    override fun getAll(driverId: UUID?): List<Driver> {
        if (driverId != null) {
            return repository.findAllById(driverId).map(DriverJpaEntity::toDomain)
        }
        return repository.findAll().map(DriverJpaEntity::toDomain)
    }
}