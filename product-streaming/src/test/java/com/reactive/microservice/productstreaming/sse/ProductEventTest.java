package com.reactive.microservice.productstreaming.sse;

import com.reactive.microservice.productstreaming.client.ProductStreamClient;
import com.reactive.microservice.productstreaming.model.ProductModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductEventTest {

    private final ProductStreamClient productStreamClient = new ProductStreamClient();

    /**
     * 2 pre-requisites before running this test.
     *  1. Make sure the application is up and running.
     *  2. Subscribe to GET /products/stream/{maxPrice} endpoint via postman to see the events sent
     * */
    @Test
    public void productEventStreamTest() {
        Flux.range(1, 100)
                .delayElements(Duration.ofMillis(100))
                .map(i -> new ProductModel(null, "product-" + i, ThreadLocalRandom.current().nextInt(1, 100)))
                .flatMap(model -> this.productStreamClient.saveProduct(Mono.just(model)))
                .subscribe();
    }
}