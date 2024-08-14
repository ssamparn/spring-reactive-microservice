package com.reactive.microservice.webfluxplayground.web.controller;

import com.reactive.microservice.webfluxplayground.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/traditional")
public class TraditionalWebController {

    private final RestClient restClient = RestClient.builder()
                                                    .baseUrl("http://localhost:7070")
                                                    .build();

    @GetMapping("/products")
    public List<Product> getProducts() {
        List<Product> list = this.restClient.get()
                                  .uri("/demo01/products")
                                  .retrieve()
                                  .body(new ParameterizedTypeReference<>() {
                                  });
        log.info("received response: {}", list);
        return list;
    }

    // This example is a classic mistake what developers are making thinking they are writing reactive programming,
    // but as a matter of fact this is a blocking piece of code wrapped around reactive objects.
    @GetMapping("/products2")
    public Flux<Product> getProducts2() {
        List<Product> list = this.restClient.get()
                                  .uri("/demo01/products")
                                  .retrieve()
                                  .body(new ParameterizedTypeReference<List<Product>>() {
                                  });
        log.info("received response: {}", list);
        return Flux.fromIterable(list);
        // This is actually synchronous blocking style of coding.
    }

    // Calling notorious product service.
    // This works similar to product service, but it crashes after 4 seconds while emitting events.
    // Make request to learn the behavior how traditional client handles the downstream service crash
    @GetMapping("/products/notorious")
    public List<Product> getNotoriousProducts() {
        List<Product> list = this.restClient.get()
                .uri("/demo01/products/notorious")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        log.info("received response: {}", list);
        return list;
    }

}
