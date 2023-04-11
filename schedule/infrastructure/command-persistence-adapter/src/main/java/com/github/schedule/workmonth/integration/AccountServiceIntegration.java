package com.github.schedule.workmonth.integration;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Service
public record AccountServiceIntegration(DiscoveryClient discoveryClient, CircuitBreakerFactory<?, ?> circuitBreakerFactory, RestTemplate restTemplate) {

    private final static String ACCOUNT_SERVICE_ID = "account-service";

    public Boolean accountExists(UUID id) {
        ServiceInstance instance = discoveryClient.getInstances(ACCOUNT_SERVICE_ID).stream()
                .findAny()
                .orElseThrow(AccountServiceIntegration::serviceNotFound);

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
            instance.getUri().toString() + "/api/accounts/" + id.toString()
        );

        return circuitBreakerFactory.create("findAccount").run(() ->
            restTemplate.getForEntity(builder.toUriString(), Void.class).getStatusCode()
        ) == HttpStatus.OK;
    }

    private static IllegalStateException serviceNotFound() {
        return new IllegalStateException(
           String.format("Could not found any instance of '%s' service", ACCOUNT_SERVICE_ID)
        );
    }
}
