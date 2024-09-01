package com.reactive.microservice.aggregatorservice.web.controller;

import com.reactive.microservice.aggregatorservice.AbstractIntegrationTest;
import com.reactive.microservice.aggregatorservice.domain.Ticker;
import com.reactive.microservice.aggregatorservice.domain.TradeAction;
import com.reactive.microservice.aggregatorservice.model.request.TradeRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.model.RegexBody;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@Slf4j
public class PortfolioAggregatorControllerTest extends AbstractIntegrationTest {

    @Test
    public void getCustomerProfileTest() {
        // given
        mockCustomerInformation("customer-service/customer-information-200.json", 200);

        // then
        getCustomerInformation(HttpStatus.OK)
                .jsonPath("$.customerId").isEqualTo(1)
                .jsonPath("$.customerName").isEqualTo("Sam")
                .jsonPath("$.balance").isEqualTo(10000)
                .jsonPath("$.holdings").isNotEmpty();
    }

    @Test
    public void customerNotFoundTest() {
        // given
        mockCustomerInformation("customer-service/customer-information-404.json", 404);

        // then
        getCustomerInformation(HttpStatus.NOT_FOUND)
                .jsonPath("$.detail").isEqualTo("Customer [id=1] is not found")
                .jsonPath("$.title").isNotEmpty();
    }

    /* *
     * Customer trade request tests are failing. Figure out why it's not able to read from mock file response hence getting a 404 ??
     * */
    @Test
    @Disabled
    public void tradeSuccessTest() {
        // mock stock-service price response
        String mockedStockResponseBody = this.resourceToString("stock-service/stock-price-200.json");
        mockServerClient
                .when(HttpRequest.request("/stock/GOOGLE"))
                .respond(
                        HttpResponse.response(mockedStockResponseBody)
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                );

        // mock customer-service trade response
        String mockedTradeResponseBody = this.resourceToString("customer-service/customer-trade-200.json");
        mockServerClient
                .when(
                        HttpRequest.request("/customers/1/trade")
                                .withMethod("POST")
                                .withBody(RegexBody.regex(".*\"price\":110.*"))
                )
                .respond(
                        HttpResponse.response(mockedTradeResponseBody)
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                );

        // then
        TradeRequest tradeRequest = new TradeRequest(Ticker.GOOGLE, 2, TradeAction.BUY);
        postTrade(tradeRequest, HttpStatus.OK)
                .jsonPath("$.balance").isEqualTo(9780)
                .jsonPath("$.totalPrice").isEqualTo(220);

    }

    /* *
     * Customer trade request tests are failing. Figure out why it's not able to read from mock file response hence getting a 404 ??
     * */
    @Test
    @Disabled
    public void tradeFailure(){
        // mock stock-service price response
        String mockedStockResponseBody = this.resourceToString("stock-service/stock-price-200.json");
        mockServerClient
                .when(HttpRequest.request("/stock/GOOGLE"))
                .respond(
                        HttpResponse.response(mockedStockResponseBody)
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                );

        // mock customer-service trade response
        String mockedTradeResponseBody = this.resourceToString("customer-service/customer-trade-400.json");
        mockServerClient
                .when(
                        HttpRequest.request("/customers/1/trade")
                                .withMethod("POST")
                                .withBody(RegexBody.regex(".*\"price\":110.*"))
                )
                .respond(
                        HttpResponse.response(mockedTradeResponseBody)
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                );

        // then
        TradeRequest tradeRequest = new TradeRequest(Ticker.GOOGLE, 2, TradeAction.BUY);
        postTrade(tradeRequest, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Customer [id=1] does not have enough funds to complete the transaction");
    }

    @Test
    public void inputValidation() {
        // no need to mock
        TradeRequest missingTicker = new TradeRequest(null, 2, TradeAction.BUY);
        postTrade(missingTicker, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Stock is required in the trade request!");

        TradeRequest missingAction = new TradeRequest(Ticker.GOOGLE, 2, null);
        postTrade(missingAction, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Trade action is required in the trade request!");

        TradeRequest invalidQuantity = new TradeRequest(Ticker.GOOGLE, -2, TradeAction.BUY);
        postTrade(invalidQuantity, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Stock quantity should be greater than zero");
    }

    private void mockCustomerInformation(String path, int statusCode) {
        String mockedCustomerPortfolioResponseBody = this.resourceToString(path);
        mockServerClient
                .when(HttpRequest.request("/customers/1"))
                .respond(
                        HttpResponse.response(mockedCustomerPortfolioResponseBody)
                                .withStatusCode(statusCode)
                                .withContentType(MediaType.APPLICATION_JSON)
                );
    }

    private WebTestClient.BodyContentSpec getCustomerInformation(HttpStatus expectedStatus) {
        return this.webTestClient.get()
                .uri("/customers/{customerId}", 1)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }

    private WebTestClient.BodyContentSpec postTrade(TradeRequest tradeRequest, HttpStatus expectedStatus) {
        return this.webTestClient.post()
                .uri("/customers/{customerId}/trade", 1)
                .bodyValue(tradeRequest)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }

}