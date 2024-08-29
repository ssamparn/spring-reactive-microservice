package com.reactive.microservice.aggregatorservice.web.controller;

import com.reactive.microservice.aggregatorservice.model.request.TradeRequest;
import com.reactive.microservice.aggregatorservice.model.response.CustomerInformationResponse;
import com.reactive.microservice.aggregatorservice.model.response.TradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class PortfolioAggregatorController {

    /* *
     * GET call will be made by the browser to Aggregator Service to see customer stock holdings / current portfolio when they access their profile.
     * GET http://localhost:8080/customers/{customerId} returns CustomerInformationResponse.
     * It calls to remote Customer Portfolio Service to endpoint http://localhost:6060/customers/{customerId}
     * CustomerInformationResponse (Integer customerId, String customerName, Integer balance, List<Holdings> holdings)
     * */
    @GetMapping("/{customerId}")
    public Mono<CustomerInformationResponse> getCustomerProfile(@PathVariable(name = "customerId") Integer customerId) {
        return Mono.empty();
    }

    /* *
     * POST call will be made by the browser to Aggregator Service to execute the trade (BUY / SELL) transaction.
     * POST /customers/{customerId}/trade receives a TradeRequest & returns a TradeResponse.
     * It calls to remote Stock Service first to endpoint http://localhost:7070/stock/{ticker} to get the current price of the stock (Ticker).
     * It then forms the StockTradeRequest to call remote Customer Portfolio service to endpoint http://localhost:6060/customers/{customerId}/trade
     * TradeRequest(Ticker ticker, TradeAction tradeAction, Integer quantity)
     * TradeResponse(Integer customerId, Ticker ticker, Integer price, Integer quantity, TradeAction traceAction, Integer totalPrice (price * quantity), Integer balance)
     * */
    @PostMapping("/{customerId}/trade")
    public Mono<TradeResponse> trade(@PathVariable("customerId") Integer customerId, @RequestBody Mono<TradeRequest> tradeRequest) {
        return Mono.empty();
    }

}
