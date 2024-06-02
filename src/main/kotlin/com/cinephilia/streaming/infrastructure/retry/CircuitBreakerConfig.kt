package com.cinephilia.streaming.infrastructure.retry

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CircuitBreakerConfig {
    @Bean
    fun streamingService(): CircuitBreakerConfig = CircuitBreakerConfig.custom()
        .failureRateThreshold(30F)
        .slidingWindowSize(10)
        .build()

    @Bean
    fun streamingServiceCircuitBreakerRegistry(): CircuitBreakerRegistry =
        CircuitBreakerRegistry.of(streamingService())

    @Bean
    fun streamingServiceCircuitBreaker(): CircuitBreaker =
        streamingServiceCircuitBreakerRegistry().circuitBreaker("streaming")
}