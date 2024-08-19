package com.reactive.microservice.webfluxplayground.connectionpooling;

import com.reactive.microservice.webfluxplayground.AbstractWebClient;
import com.reactive.microservice.webfluxplayground.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.test.StepVerifier;

@Slf4j
public class HttpConnectionPoolingTest extends AbstractWebClient {

    Integer poolSize = 10000;
    ConnectionProvider provider = ConnectionProvider.builder("sassaman")
            .lifo()
            .maxConnections(poolSize)
            .pendingAcquireMaxCount(poolSize * 5)
            .build();
    HttpClient httpClient = HttpClient.create(provider)
            .compress(true)
            .keepAlive(true);

    private final WebClient client = WebClient.builder()
            .baseUrl("http://localhost:7070/demo03")
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();

    /**
     * Concurrent Requests Demo:
     *   Change the value from 1 to multiple e.g. 10. Observe the network logs of the external-service (in this case swagger endpoint) via netstat command to observe 2 things.
     *      1. See how for each request a new connection to external-service is being established.
     *      2. See how for each request an outbound port from the host (client machine) is being opened.
     * */
    @Test
    public void concurrentRequests() {
        Integer maxRequests = 3; //
        Flux.range(1, maxRequests)
                .flatMap(this::getProduct) // V. Imp Note: flatMap() internally subscribes to the publisher in parallel
                .collectList()
                .as(StepVerifier::create)
                .assertNext(l -> Assertions.assertEquals(maxRequests, l.size()))
                .expectComplete()
                .verify();
    }

    /* *
     * Product Service: Provides the product name for the given product id (1 to 20,000). It is a slow service. Response time will take up to 1 second.
     * GET http://localhost:7070/demo03/product/{productId}
     * */
    private Mono<Product> getProduct(int productId) {
        return this.client.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono(Product.class);
    }
}