package com.reactive.microservice.webfluxplayground.section02;

import com.reactive.microservice.webfluxplayground.section02.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;

@Slf4j
public class ProductRepositoryTest extends AbstractTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void findByPriceRange() {
        this.productRepository.findByPriceBetween(500, 1000)
                .doOnNext(product -> log.info("product received: {}", product))
                .as(StepVerifier::create)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @Test
    public void findAllByPageable() {
        Pageable pageRequest = PageRequest
                .of(0, 3) // pageNumber 0 means its first page. As page index in Pageable starts with 0.
                .withSort(Sort.by("price")
                        .ascending()
                );

        this.productRepository.findAllBy(pageRequest)
                .doOnNext(product -> log.info("product received: {}", product))
                .as(StepVerifier::create)
                .assertNext(product -> Assertions.assertEquals(200, product.getPrice()))
                .assertNext(product -> Assertions.assertEquals(250, product.getPrice()))
                .assertNext(product -> Assertions.assertEquals(300, product.getPrice()))
                .expectComplete()
                .verify();

    }
}
