package com.pl.platform.svc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DriverApplication

fun main(args: Array<String>) {
	runApplication<DriverApplication>(*args)
}
