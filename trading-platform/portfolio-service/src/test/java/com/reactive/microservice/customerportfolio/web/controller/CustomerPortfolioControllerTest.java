package com.reactive.microservice.customerportfolio.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerPortfolioControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void getCustomerInformation() {
        this.getCustomer(1, HttpStatus.OK)
               .jsonPath("$.customerName").isEqualTo("Sam")
               .jsonPath("$.balance").isEqualTo(10000)
               .jsonPath("$.holdings").isEmpty(); // empty holdings as we have not carried out any trade.
    }

    @Test
    void trade() {

    }

    private WebTestClient.BodyContentSpec getCustomer(Integer customerId, HttpStatus expectedStatus) {
        return this.client.get()
                .uri("/customers/{customerId}", customerId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }

//    private WebTestClient.BodyContentSpec trade(Integer customerId, StockTradeRequest request, HttpStatus expectedStatus) {
//        return this.client.post()
//                .uri("/customers/{customerId}/trade", customerId)
//                .bodyValue(request)
//                .exchange()
//                .expectStatus().isEqualTo(expectedStatus)
//                .expectBody()
//                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
//    }
}