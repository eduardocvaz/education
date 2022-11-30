package com.labcomu.edu.client;

import com.labcomu.edu.configuration.EduProperties;
import com.labcomu.edu.exceptions.CircuitBreakerOpenException;
import com.labcomu.edu.resource.Organization;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
@Validated
public class OrgGateway {
    private final String fetchOrganizationUrl;

    private final WebClient.Builder webClientBuilder;

    private final ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    public OrgGateway(final WebClient.Builder webClientBuilder,
                      final EduProperties properties, ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory) {
        this.webClientBuilder = webClientBuilder;
        this.fetchOrganizationUrl = properties.getUrl().getFetchOrganizationDetails();
        this.reactiveCircuitBreakerFactory = reactiveCircuitBreakerFactory;
    }


    public Organization getOrganization(@NotNull final String url) {
        return webClientBuilder.build()
                .get()
                .uri(fetchOrganizationUrl, url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Organization.class)
                .transform(it -> {
                    ReactiveCircuitBreaker rcb = reactiveCircuitBreakerFactory.create("customer-service");
                    return rcb.run(
                            it,
                            throwable -> {
                                if (throwable instanceof TimeoutException) {
                                    log.error("throwable in OrgGateway {}", throwable.toString());
                                    return Mono.error(new CircuitBreakerOpenException("Serviço esta demorando muito para responder"));
                                }
                                log.error("throwable in OrgGateway {}", throwable.toString());
                                return Mono.error(new CircuitBreakerOpenException("Serviço Temporarialmente Indisponivel"));
                            }
                            );
                })
                .block();
    }
}
