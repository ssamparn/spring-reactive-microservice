package com.reactive.microservice.webfluxplayground.client;

import com.reactive.microservice.webfluxplayground.AbstractWebClient;
import com.reactive.microservice.webfluxplayground.client.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static com.reactive.microservice.webfluxplayground.util.TestUtil.onNext;

public class FluxClientTest extends AbstractWebClient {

    private final WebClient client = createWebClient();

    /* *
     * Demo GET requests receiving streaming response
     * GET: http://localhost:7070/demo02/lec02/product/stream
     * Provide product details as stream - 1 to 10. 500ms delay for each item.
     * */
    @Test
    public void getProductStreams() {
        this.client.get()
                .uri("/lec02/product/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}
