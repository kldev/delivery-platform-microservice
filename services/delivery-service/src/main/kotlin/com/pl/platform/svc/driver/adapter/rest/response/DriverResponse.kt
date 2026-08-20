package com.pl.platform.svc.driver.adapter.rest.response

import com.pl.platform.svc.driver.domain.Driver
import java.util.UUID

data class DriverResponse(val id: UUID, val firstName: String,val lastName: String, val phoneNumber: String ) {
    companion object {
        fun from(driver: Driver): DriverResponse {
            return DriverResponse(driver.id.value,
                driver.firstName,
                driver.lastName,
                driver.phoneNumber);
        }
    }
}