package com.pl.platform.svc.driver.application.command

data class CreateDriverCommand(val firstName: String,
                               val lastName: String,
                               val phoneNumber: String,
                               val email: String)