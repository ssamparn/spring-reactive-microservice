package com.reactive.microservice.customerportfolio.web.controller;

import com.reactive.microservice.customerportfolio.model.request.StockTradeRequest;
import com.reactive.microservice.customerportfolio.model.response.StockTradeResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Customer Service: Customer Service is not about making CRUD operations on customers.
 * It is about managing the customer portfolio.
 * */
@RestController
@RequestMapping("/customers")
public class CustomerPortfolioController {

    /* *
     * GET call will be made by Aggregator Service to see the customer stock holdings / current portfolio when they access their profile.
     *
     * GET /customers/{customerId} returns CustomerInformationResponse.
     *
     * CustomerInformationResponse (customerId, customerName, balance, List<Holdings>)
     *
     * Holding (ticker, quantity) => Retrieved from Portfolio Item Table.
     *
     * Ticker => APPLE, GOOGLE, AMAZON, MICROSOFT
     *
     * */



    /* *
     * POST call will be made by Aggregator Service to finish BUY or SELL of Trades.
     *
     * POST /customers/{customerId}/trade received a StockTradeRequest & returns StockTradeResponse
     *
     * StockTradeRequest(ticker, price (current price of the ticker), quantity, traceAction (BUY or SELL)
     *
     * StockTradeResponse(customerId, ticker, price, quantity, traceAction, totalPrice (price * quantity), balance)
     * */

    @PostMapping("/{customerId}/trade")
    public Mono<StockTradeResponse> trade(@RequestBody Mono<StockTradeRequest> stockTradeRequestMono) {
        return Mono.empty();
    }

}
