package com.reactive.microservice.productstreaming.service;

import com.reactive.microservice.productstreaming.mapper.EntityModelMapper;
import com.reactive.microservice.productstreaming.model.ProductModel;
import com.reactive.microservice.productstreaming.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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
}
