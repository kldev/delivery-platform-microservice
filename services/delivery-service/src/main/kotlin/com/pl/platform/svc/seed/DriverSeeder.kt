package com.pl.platform.svc.seed
import com.pl.platform.svc.driver.domain.Driver
import com.pl.platform.svc.driver.port.DriverRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional

@Profile("dev")
@Component
class DriverSeeder(private val driverRepository: DriverRepository) {

    @Transactional
    @EventListener(        ApplicationReadyEvent::class)
        fun seed() {
        if (driverRepository.getAll().isNotEmpty())
            return

        val driverA = Driver.create(firstName = "John" , lastName = "Smith",  phoneNumber = "+1530200111")
        val driverB = Driver.create(firstName = "Annie" , lastName = "Stockhouse",  phoneNumber = "+1530200221")
        val driverC = Driver.create(firstName = "Chandler" , lastName = "Marius",  phoneNumber = "+15302001121")

        driverRepository.create(driverA);
        driverRepository.create(driverB);
        driverRepository.create(driverC);
    }

}