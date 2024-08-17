package com.reactive.microservice.webfluxplayground.client;

import com.reactive.microservice.webfluxplayground.AbstractWebClient;
import com.reactive.microservice.webfluxplayground.client.model.Product;
import com.reactive.microservice.webfluxplayground.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.reactive.microservice.webfluxplayground.util.TestUtil.onComplete;
import static com.reactive.microservice.webfluxplayground.util.TestUtil.onError;
import static com.reactive.microservice.webfluxplayground.util.TestUtil.onNext;

public class MonoClientTest extends AbstractWebClient {

    private final WebClient client = createWebClient();

    /* *
     * Demo simple GET request
     * */
    @Test
    public void getProductMono() {
        this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/lec01/product/{productId}").build(1))
                .retrieve()
                .bodyToMono(Product.class)
                .subscribe(
                        onNext(),
                        onError(),
                        onComplete()
                );

        TestUtil.sleepSeconds(2);
    }

    /* *
     * Demo non-blocking concurrent GET requests.
     *    - WebClient is a wrapper around reactor-netty
     *    - It uses 1 thread per CPU
     *    - It is Non-Blocking
     * */

    @Test
    public void getProductsMonoConcurrentRequests() {
        for (int i = 1; i <= 100; i++) { // all 100 products we will get within a second
            int finalI = i;
            this.client.get()
                    .uri(uriBuilder -> uriBuilder.path("/lec01/product/{productId}").build(finalI))
                    .retrieve()
                    .bodyToMono(Product.class)
                    .subscribe(
                            onNext(),
                            onError(),
                            onComplete()
                    );
        }

        // If you observe clearly, all 100 requests are executed by 12 threads (equal to the number of CPU Cores) and all 100 requests happened more or less at the same time.
        // That is why order is not maintained. This is exactly what non-blocking IO is.
        TestUtil.sleepSeconds(2);
    }

    /* *
     * Demo simple POST request - BodyValue
     * */
    @Test
    public void postProductWithBodyValue() {
        Product product = new Product(null, "iphone", 1000);

        this.client.post()
                .uri("/lec03/product")
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    /* *
     * Demo simple POST request - BodyPublisher.
     * Sometimes the request body might not be present in the memory. In some cases, we might retrieve the request body from an external service or from a database and send it as a publisher.
     * In those cases using BodyPublisher is helpful.
     * */
    @Test
    public void postProductBodyPublisher() {
        Mono<Product> productMono = Mono.fromSupplier(() -> new Product(null, "iphone", 1000))
                .delayElement(Duration.ofMillis(500)); // Assuming it takes 500ms to form/fetch the request body

        this.client.post()
                .uri("/lec03/product")
                .body(productMono, Product.class)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    /* *
     * Demo simple GET request with mandatory header caller-id
     * */
    @Test
    public void getProductWithMandatoryHeaderMono() {
        this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/lec04/product/{productId}").build(1))
                .headers(headers -> headers.add("caller-id", UUID.randomUUID().toString()))
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    /* *
     * Demo simple GET request with mandatory header caller-id sent as a Map
     * */
    @Test
    public void getProductWithMandatoryHeaderAsMapMono() {
        Map<String, String> headersMap = new LinkedHashMap<>();
        headersMap.put("caller-id", UUID.randomUUID().toString());
        headersMap.put("some-key", "some-value");

        this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/lec04/product/{productId}").build(1))
                .headers(headers -> headers.setAll(headersMap))
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}
