package com.pl.platform.svc.test.fixture


import com.pl.platform.svc.driver.domain.Driver
import java.security.SecureRandom

class DriverTestFactory() {
    companion object {
        private val random = SecureRandom()
        fun create(
            firstName: String = "John",
            lastName: String = "Connor",
            phoneNumber: String = "+48" + random.nextInt(1000) + random.nextInt(1000) + random.nextInt(100),
        ): Driver =
            Driver.create(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
            )
    }
}