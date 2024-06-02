package com.cinephilia.streaming

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.retry.annotation.EnableRetry
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme

@SpringBootApplication
@SecurityScheme(
	name = "Auth",
	type = SecuritySchemeType.HTTP,
//	format = "JWT",
	scheme = "auth"
)
@EnableRetry
class StreamingApplication
fun main(args: Array<String>) {
	runApplication<StreamingApplication>(*args)
}