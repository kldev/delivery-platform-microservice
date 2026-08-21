package com.pl.platform.svc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class SettlementApplication

fun main(args: Array<String>) {
	runApplication<SettlementApplication>(*args)
}
