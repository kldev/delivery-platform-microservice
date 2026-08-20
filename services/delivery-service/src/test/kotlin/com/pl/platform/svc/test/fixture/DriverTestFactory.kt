package com.pl.platform.svc.test.fixture


import com.pl.platform.svc.driver.domain.Driver

object DriverTestFactory {
    fun create(
        firstName: String = "John",
        lastName: String = "Connor",
        phoneNumber: String = "+485190211222"
    ): Driver =
        Driver.create(
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
        )
}