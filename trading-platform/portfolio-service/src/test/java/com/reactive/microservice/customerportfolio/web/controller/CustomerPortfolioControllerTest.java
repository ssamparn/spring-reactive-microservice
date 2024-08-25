package com.reactive.microservice.customerportfolio.web.controller;

import com.reactive.microservice.customerportfolio.domain.Ticker;
import com.reactive.microservice.customerportfolio.domain.TradeAction;
import com.reactive.microservice.customerportfolio.model.request.StockTradeRequest;
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
    public void buyAndSell() {
        // buy
        StockTradeRequest buyRequest1 = new StockTradeRequest(Ticker.GOOGLE, 100, 5, TradeAction.BUY);
        this.trade(2, buyRequest1, HttpStatus.OK)
                .jsonPath("$.balance").isEqualTo(9500)
                .jsonPath("$.totalPrice").isEqualTo(500);

        StockTradeRequest buyRequest2 = new StockTradeRequest(Ticker.GOOGLE, 100, 10, TradeAction.BUY);
        this.trade(2, buyRequest2, HttpStatus.OK)
                .jsonPath("$.balance").isEqualTo(8500)
                .jsonPath("$.totalPrice").isEqualTo(1000);

        // check the holdings
        this.getCustomer(2, HttpStatus.OK)
                .jsonPath("$.holdings").isNotEmpty()
                .jsonPath("$.holdings.length()").isEqualTo(1)
                .jsonPath("$.holdings[0].ticker").isEqualTo("GOOGLE")
                .jsonPath("$.holdings[0].quantity").isEqualTo(15);

        // sell
        StockTradeRequest sellRequest1 = new StockTradeRequest(Ticker.GOOGLE, 110, 5, TradeAction.SELL);
        this.trade(2, sellRequest1, HttpStatus.OK)
                .jsonPath("$.balance").isEqualTo(9050) // previous balance (8500) + 550 = 9050
                .jsonPath("$.totalPrice").isEqualTo(550);

        StockTradeRequest sellRequest2 = new StockTradeRequest(Ticker.GOOGLE, 110, 10, TradeAction.SELL);
        this.trade(2, sellRequest2, HttpStatus.OK)
                .jsonPath("$.balance").isEqualTo(10150) // previous balance (9050) + 1100 = 10150
                .jsonPath("$.totalPrice").isEqualTo(1100);

        // check the holdings
        this.getCustomer(2, HttpStatus.OK)
                .jsonPath("$.holdings").isNotEmpty() // even if we sold GOOGLE stock, we are not deleting the entry. So the ticker should be present, but the quantity should be 0.
                .jsonPath("$.holdings.length()").isEqualTo(1) // intentional
                .jsonPath("$.holdings[0].ticker").isEqualTo("GOOGLE")
                .jsonPath("$.holdings[0].quantity").isEqualTo(0);
    }

    @Test
    public void customerNotFound() {
        this.getCustomer(10, HttpStatus.NOT_FOUND)
                .jsonPath("$.detail").isEqualTo("Customer [id=10] is not found");

        StockTradeRequest sellRequest = new StockTradeRequest(Ticker.GOOGLE, 110, 5, TradeAction.SELL);
        this.trade(10, sellRequest, HttpStatus.NOT_FOUND)
                .jsonPath("$.detail").isEqualTo("Customer [id=10] is not found");
    }

    @Test
    public void insufficientBalance() {
        StockTradeRequest buyRequest = new StockTradeRequest(Ticker.GOOGLE, 100, 101, TradeAction.BUY);
        trade(3, buyRequest, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Customer [id=3] does not have enough funds to complete the transaction");
    }

    @Test
    public void insufficientShares() {
        StockTradeRequest sellRequest = new StockTradeRequest(Ticker.GOOGLE, 100, 1, TradeAction.SELL);
        trade(3, sellRequest, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Customer [id=3] does not have enough shares to complete the transaction");
    }

    private WebTestClient.BodyContentSpec getCustomer(Integer customerId, HttpStatus expectedStatus) {
        return this.client.get()
                .uri("/customers/{customerId}", customerId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }

    private WebTestClient.BodyContentSpec trade(Integer customerId, StockTradeRequest request, HttpStatus expectedStatus) {
        return this.client.post()
                .uri("/customers/{customerId}/trade", customerId)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }
}