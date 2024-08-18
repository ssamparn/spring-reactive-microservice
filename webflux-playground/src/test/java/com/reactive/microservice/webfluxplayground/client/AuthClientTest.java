package com.reactive.microservice.webfluxplayground.client;

import com.reactive.microservice.webfluxplayground.AbstractWebClient;
import com.reactive.microservice.webfluxplayground.client.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.reactive.microservice.webfluxplayground.util.TestUtil.onNext;

@Slf4j
public class AuthClientTest extends AbstractWebClient {

    private final WebClient client = createWebClient();

    /* *
     * Demo invoking a Rest Service protected with Basic Authentication
     * GET: http://localhost:7070/demo02/lec07/product/{productId}
     * Provide product details for the given product id. It supports product id between 1 and 100.
     * It expects basic credentials to be sent. username: java, password: secret.
     * Returns 401 otherwise!
     * */

    @Test
    public void basicAuthProductService() {
        this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/lec07/product/{productId}").build(1))
                .headers(headers -> headers.setBasicAuth("java", "secret"))
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    /* *
     * Demo invoking a Rest Service protected with a Bearer Token Authentication
     * GET: http://localhost:7070/demo02/lec08/product/{productId}
     * Provide product details for the given product id. It supports product id between 1 and 100.
     * It expects bearer token to be sent. Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
     * Returns 401 otherwise!
     * */

    @Test
    public void bearerTokenProductService() {
        this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/lec08/product/{productId}").build(1))
                .headers(headers -> headers.setBearerAuth("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"))
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    /* *
     * Demo invoking a Rest Service protected with a Bearer Token Authentication but WebClient will make use of Exchange Filter Function in order to generate new bearer token every time it makes a request
     * GET: http://localhost:7070/demo02/lec09/product/{productId}
     * Provide product details for the given product id. It supports product id between 1 and 100.
     * It expects bearer token to be sent. Authorization: Bearer [generate new token].
     * Send new token every time using UUID.randomUUID().toString().replace("-", "")!
     * */
    @Test
    public void bearerTokenWithExchangeFilterFunctionProductService() {
        WebClient client = createWebClient(f -> f.filter(tokenGenerator()));

        client
            .get()
            .uri(uriBuilder -> uriBuilder.path("/lec09/product/{productId}").build(1))
            .retrieve()
            .bodyToMono(Product.class)
            .doOnNext(onNext())
            .then()
            .as(StepVerifier::create)
            .expectComplete()
            .verify();
    }

    /**
     * Assignment: Create an ExchangeFilterFunction to log Http Request Method and the Url that WebClient making requests to.
     *             Also log the Response Status Code.
     * */
    @Test
    public void logHttpMethodAndUrlExchangeFilterFunctionProductService() {
        WebClient client = createWebClient(f -> f
                .filter(tokenGenerator())
                .filter(logRequests())
                .filter(logResponses()));

        client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/lec09/product/{productId}").build(1))
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    /* *
     * WebClient Attribute Demo:
     *  1. One filter function can pass some information to another filter function using WebClient attributes.
     *  2. We can also pass these attributes from the service classes.
     *
     * e.g: In the below example we can decide at the service layer itself if we want to log the request details or not.
     * */
    @Test
    public void webClientAttributeExchangeFilterFunctionProductService() {
        WebClient client = createWebClient(f -> f
                .filter(tokenGenerator())
                .filter(logRequests())
                .filter(logResponses()));

        client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/lec09/product/{productId}").build(1))
                .attribute("enable-logging", true)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    private ExchangeFilterFunction tokenGenerator() {
        return (request, next) -> {
            String token = UUID.randomUUID().toString().replace("-", "");
            log.info("generated token: {}", token);
            ClientRequest decoratedRequest = ClientRequest.from(request).headers(h -> h.setBearerAuth(token)).build();
            return next.exchange(decoratedRequest);
        };
    }

    private ExchangeFilterFunction logRequests() {
        return ((request, next) -> {
            boolean isLoggingEnabled = (Boolean) request.attributes().getOrDefault("enable-logging", false);
            if (isLoggingEnabled) {
                log.info("Request: {} {}", request.method(), request.url());
            }
            return next.exchange(request);
        });
    }

    private ExchangeFilterFunction logResponses() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("Response status code: {}", response.statusCode());
            return Mono.just(response);
        });
    }

}