package com.reactive.microservice.webfluxplayground.client;

import com.reactive.microservice.webfluxplayground.AbstractWebClient;
import com.reactive.microservice.webfluxplayground.client.model.CalculatorResponse;
import com.reactive.microservice.webfluxplayground.model.Product;
import com.reactive.microservice.webfluxplayground.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.reactive.microservice.webfluxplayground.util.TestUtil.onComplete;
import static com.reactive.microservice.webfluxplayground.util.TestUtil.onError;
import static com.reactive.microservice.webfluxplayground.util.TestUtil.onNext;

@Slf4j
public class MonoClientTest extends AbstractWebClient {

    private final WebClient client = createWebClient();

    /* *
     * Demo simple GET request
     * GET: http://localhost:7070/demo02/lec01/product/{productId}
     * Provide product details for the given product id. It supports product id between 1 and 100. Each call will take 1 second.
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
     * GET: http://localhost:7070/demo02/lec01/product/{productId}
     * Provide product details for the given product id. It supports product id between 1 and 100. Each call will take 1 second.
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
     * POST: http://localhost:7070/demo02/lec03/product
     * It is a simple endpoint to accept a POST request for a product! It will take 1 second to respond.
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
     * GET: http://localhost:7070/demo02/lec04/product/{productId}
     * Provide product details for the given product id between 1 and 100. This header "caller-id" should be present. Example: "caller-id: order-service"
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

    /* *
     * Demo error handling while calling a remote service with GET request
     * GET: http://localhost:7070/demo02/lec05/calculator/{first}/{second}
     * Does the math operation and returns the response. first and second parameter values should be > 0. Send one of these "+, -, *, /" as header value. Example: "operation: +"
     * */
    @Test
    public void getCalculatedValueAndHandleErrorWithRetrieve() {
        this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/lec05/calculator/{first}/{second}").build(10, 20))
                .headers(headers -> headers.add("operation", "@")) // valid operation values can be "+", "-", "*", "/"
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .doOnError(WebClientResponseException.class, ex -> log.info("{}", ex.getResponseBodyAs(ProblemDetail.class))) // V.Imp error handling line
                .onErrorReturn(WebClientResponseException.InternalServerError.class, new CalculatorResponse(0, 0, null, 0.0))
                .onErrorReturn(WebClientResponseException.BadRequest.class, new CalculatorResponse(0, 0, null, -1.0))
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    /* *
     * So far we have been using retrieve() and it works great so far. But there might be some scenarios where we might want to access
     *    - Response Headers
     *    - Cookies
     *    - Status Code
     * In such scenarios, use exchange() instead of retrieve().
     * */

    @Test
    public void getCalculatedValueAndHandleErrorWithExchange() {
        this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/lec05/calculator/{first}/{second}").build(10, 20))
                .headers(headers -> headers.add("operation", "+")) // valid operation values can be "+", "-", "*", "/"
                .exchangeToMono(clientResponse -> {
                    log.info("Status Code: {}", clientResponse.statusCode());
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.bodyToMono(ProblemDetail.class)
                                .doOnNext(problemDetail -> log.info("Error: {}", problemDetail))
                                .then(Mono.empty());
                    }
                    return clientResponse.bodyToMono(CalculatorResponse.class);
                })
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    /* *
     * Demo query param with GET request
     * GET: http://localhost:7070/demo02/lec06/calculator?first=10&second=20&operation=+
     * Does the math operation and returns the response. It expects 3 parameters. first, second & operation
     * first, second parameter values should be > 0. operation should be one of "+, -, *, /"
     * */
    @Test
    public void getCalculatedValueWithQueryParamWithUriBuilder() {
        this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/lec06/calculator").query("first={first}&second={second}&operation={operation}").build(10, 20, "+"))
                .headers(headers -> headers.add("operation", "@")) // valid operation values can be "+", "-", "*", "/"
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    public void getCalculatedValueWithQueryParamWithAsMap() {
        var queryParams = Map.of(
                "first", 10,
                "second", 20,
                "operation", "*"
        );
        this.client.get()
                .uri(uriBuilder -> uriBuilder.path("/lec06/calculator").query("first={first}&second={second}&operation={operation}").build(queryParams))
                .headers(headers -> headers.add("operation", "@")) // valid operation values can be "+", "-", "*", "/"
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}
