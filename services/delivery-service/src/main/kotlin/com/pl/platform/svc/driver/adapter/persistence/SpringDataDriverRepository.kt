package com.pl.platform.svc.driver.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataDriverRepository : JpaRepository<DriverJpaEntity, UUID> {
    fun findByPhoneNumber(phoneNumber: String): DriverJpaEntity?
    fun existsByPhoneNumber(phoneNumber: String): Boolean
}