package com.reactive.microservice.customerportfolio.web.controller;

import com.reactive.microservice.customerportfolio.model.request.StockTradeRequest;
import com.reactive.microservice.customerportfolio.model.response.CustomerInformationResponse;
import com.reactive.microservice.customerportfolio.model.response.StockTradeResponse;
import com.reactive.microservice.customerportfolio.service.CustomerPortfolioService;
import com.reactive.microservice.customerportfolio.service.StockTradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/* *
 * Customer Service: Customer Service is not about making CRUD operations on customers.
 * It is about managing the customer portfolio.
 * */

@Slf4j
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerPortfolioController {

    private final CustomerPortfolioService customerPortfolioService;
    private final StockTradeService stockTradeService;

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

    @GetMapping("/{customerId}")
    public Mono<CustomerInformationResponse> getCustomerInformation(@PathVariable(name = "customerId") Integer customerId) {
        return customerPortfolioService.getCustomerInformationApproach1(customerId);
    }

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
    public Mono<StockTradeResponse> trade(@PathVariable(name = "customerId") Integer customerId, @RequestBody Mono<StockTradeRequest> stockTradeRequestMono) {
        return stockTradeRequestMono
                .flatMap(stockTradeRequest -> this.stockTradeService.trade(customerId, stockTradeRequest));
    }

}
