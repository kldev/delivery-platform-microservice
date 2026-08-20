package com.pl.platform.svc.driver.port

import com.pl.platform.svc.driver.domain.Driver
import com.pl.platform.svc.driver.domain.DriverId

interface DriverRepository {

    fun findById(id: DriverId): Driver?

    fun findByPhoneNumber(phoneNumber: String): Driver?

    fun create(driver: Driver)

    fun update(driver: Driver)

    fun existsByPhoneNumber(phoneNumber: String): Boolean
}