package com.labcomu.edu.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

public class CircuitBreakerConfiguration {
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> customerServiceCusomtizer() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .cancelRunningFuture(true)
                .timeoutDuration(Duration.ofMillis(2000))
                .build();

        return factory -> {
            factory.configure(builder -> builder
                    .timeLimiterConfig(config)
                    .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()), "customer-service");
        };
    }
}
