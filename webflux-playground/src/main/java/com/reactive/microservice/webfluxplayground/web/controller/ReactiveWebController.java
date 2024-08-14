package com.reactive.microservice.webfluxplayground.web.controller;

import com.reactive.microservice.webfluxplayground.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/reactive")
public class ReactiveWebController {

    private final WebClient webClient = WebClient.builder()
                                                 .baseUrl("http://localhost:7070")
                                                 .build();

    // sends streams of events to the console
    @GetMapping("/products")
    public Flux<Product> getProducts() {
        return this.webClient.get()
                             .uri("/demo01/products")
                             .retrieve()
                             .bodyToFlux(Product.class)
                             .doOnNext(p -> log.info("received event: {}", p));
    }

    // sends streams of events to the console & browser. produces has been provided as MediaType.TEXT_EVENT_STREAM_VALUE
    // for browser to understand the streaming events
    @GetMapping(value = "products/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> getProductsStream() {
        return this.webClient.get()
                             .uri("/demo01/products")
                             .retrieve()
                             .bodyToFlux(Product.class)
                             .doOnNext(p -> log.info("received event: {}", p));
    }

    // Calling notorious product service.
    // This works similar to product service, but it crashes after 4 seconds while emitting events.
    // Make request to learn the behavior how reactive client handles the downstream service crash.
    @GetMapping(value = "/products/notorious", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> getNotoriousProducts() {
        return this.webClient.get()
                .uri("/demo01/products/notorious")
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnNext(p -> log.info("received event: {}", p));
    }
}
