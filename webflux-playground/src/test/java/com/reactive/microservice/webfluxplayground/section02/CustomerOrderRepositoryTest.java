package com.reactive.microservice.webfluxplayground.section02;

import com.reactive.microservice.webfluxplayground.section02.repository.CustomerOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@Slf4j
public class CustomerOrderRepositoryTest extends AbstractTest {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Test
    public void productsOrderedByCustomer() {
       this.customerOrderRepository.getProductsOrderedByCustomer("mike")
                      .doOnNext(product -> log.info("product received: {}", product))
                      .as(StepVerifier::create)
                      .expectNextCount(2)
                      .expectComplete()
                      .verify();
    }

    @Test
    public void orderDetailsByProduct() {
        this.customerOrderRepository.getOrderDetailsByProduct("iphone 20")
                       .doOnNext(model -> log.info("order details received: {}", model))
                       .as(StepVerifier::create)
                       .assertNext(model -> Assertions.assertEquals(975, model.amount()))
                       .assertNext(model -> Assertions.assertEquals(950, model.amount()))
                       .expectComplete()
                       .verify();
    }

}
