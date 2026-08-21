package com.pl.platform.svc.driver.application.handler

import com.pl.platform.svc.driver.adapter.rest.response.DriverResponse
import com.pl.platform.svc.driver.port.DriverRepository
import org.springframework.stereotype.Service

@Service
class GetAllDriversHandler(private val driverRepository: DriverRepository) {
    fun handle(): List<DriverResponse> {
        return driverRepository.getAll().map(DriverResponse::from);
    }
}