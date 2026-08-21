package com.pl.platform.svc.driver.application.handler
import com.pl.platform.svc.driver.adapter.rest.response.DriverResponse
import com.pl.platform.svc.driver.application.command.CreateDriverCommand
import com.pl.platform.svc.driver.domain.Driver
import com.pl.platform.svc.driver.port.DriverRepository
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional

@Component
class CreateDriverHandler(
    private val driverRepository: DriverRepository
) {
    @Transactional
    fun handle(command: CreateDriverCommand): DriverResponse {
        val driver = Driver.create(
            firstName = command.firstName.trim(),
            lastName = command.lastName.trim(),
            phoneNumber = command.phoneNumber.trim()
        )

        if (driverRepository.existsByPhoneNumber(driver.phoneNumber)) {
            throw IllegalArgumentException("The phone number already exists")
        }
        driverRepository.create(driver)

        return DriverResponse.from(driver)
    }
}