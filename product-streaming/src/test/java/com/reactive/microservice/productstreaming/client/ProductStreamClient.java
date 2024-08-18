package com.reactive.microservice.productstreaming.client;

import com.reactive.microservice.productstreaming.model.ProductModel;
import com.reactive.microservice.productstreaming.model.UploadResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * We will be using this client in our test class to upload 1 million products.
 * */
public class ProductStreamClient {

    private final WebClient productStreamClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    public Mono<UploadResponse> uploadProducts(Flux<ProductModel> productModelFlux) {
        return this.productStreamClient.post()
                .uri("/products/upload")
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(productModelFlux, ProductModel.class)
                .retrieve()
                .bodyToMono(UploadResponse.class);
    }
}
