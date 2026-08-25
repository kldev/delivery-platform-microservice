package com.pl.platform.svc.driver.adapter.rest.request

import com.pl.platform.svc.driver.application.command.CreateDriverCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateDriverRequest(

    @field:NotBlank
    @field:Size(max = 100)
    val firstName: String,

    @field:NotBlank
    @field:Size(max = 100)
    val lastName: String,

    @field:NotBlank
    @field:Size(max = 100)
    val phoneNumber: String,

    @field:NotBlank
    @field:Size(max = 320)
    val email: String
) {

    fun toCommand(): CreateDriverCommand {
        return CreateDriverCommand(firstName, lastName, phoneNumber, email)
    }

}