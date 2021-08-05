package com.external.OAuth2ResourceServer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import java.util.TimeZone

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class OAuth2ResourceServerApplication

fun main(args: Array<String>) {
	runApplication<OAuth2ResourceServerApplication>(*args)

	TimeZone.setDefault(TimeZone.getTimeZone("Europe/Zagreb"))
	println("Application started successfully!")
}
