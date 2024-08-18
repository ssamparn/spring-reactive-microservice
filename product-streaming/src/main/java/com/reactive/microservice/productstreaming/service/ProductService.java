package com.reactive.microservice.productstreaming.service;

import com.reactive.microservice.productstreaming.mapper.EntityModelMapper;
import com.reactive.microservice.productstreaming.model.ProductModel;
import com.reactive.microservice.productstreaming.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final Sinks.Many<ProductModel> productModelSink;

    public Flux<ProductModel> saveProducts(Flux<ProductModel> productModelFlux) {
        return productModelFlux
                .map(EntityModelMapper::toEntity)
                .as(this.productRepository::saveAll)
                .map(EntityModelMapper::toModel);
    }

    public Mono<Long> getProductsCount() {
        return this.productRepository.count();
    }

    public Flux<ProductModel> getProducts() {
        return this.productRepository.findAll()
                .map(EntityModelMapper::toModel);
    }

    public Mono<ProductModel> saveProduct(Mono<ProductModel> productModelMono) {
        return productModelMono
                .map(EntityModelMapper::toEntity)
                .flatMap(this.productRepository::save)
                .map(EntityModelMapper::toModel)
                .doOnNext(productModelSink::tryEmitNext);
    }

    public Flux<ProductModel> productStream() {
        return this.productModelSink.asFlux();
    }
}
