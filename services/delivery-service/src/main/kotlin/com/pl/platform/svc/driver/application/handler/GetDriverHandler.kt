package com.pl.platform.svc.driver.application.handler

import com.pl.platform.common.exception.EntityNotFoundException
import com.pl.platform.common.exception.EntityType
import com.pl.platform.svc.driver.adapter.rest.response.DriverResponse
import com.pl.platform.svc.driver.domain.DriverId
import com.pl.platform.svc.driver.port.DriverRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class GetDriverHandler(private val driverRepository: DriverRepository) {
    @Transactional(readOnly = true)
    fun handle(driverId: UUID): DriverResponse {
        val driver = driverRepository.findById(DriverId(driverId))
            ?: throw EntityNotFoundException(EntityType.DRIVER, driverId)

        return DriverResponse.from(driver)
    }
}