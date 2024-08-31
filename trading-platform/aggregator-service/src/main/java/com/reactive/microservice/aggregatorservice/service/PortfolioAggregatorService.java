package com.reactive.microservice.aggregatorservice.service;

import com.reactive.microservice.aggregatorservice.client.CustomerServiceClient;
import com.reactive.microservice.aggregatorservice.client.StockServiceClient;
import com.reactive.microservice.aggregatorservice.mapper.CustomerPortfolioMapper;
import com.reactive.microservice.aggregatorservice.model.request.TradeRequest;
import com.reactive.microservice.aggregatorservice.model.response.CustomerPortfolioResponse;
import com.reactive.microservice.aggregatorservice.model.response.TradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PortfolioAggregatorService {

    private final CustomerServiceClient customerServiceClient;
    private final CustomerPortfolioMapper customerPortfolioMapper;
    private final StockServiceClient stockServiceClient;


    public Mono<CustomerPortfolioResponse> getCustomerPortfolio(Integer customerId) {
        return this.customerServiceClient.getCustomerPortfolio(customerId)
                .map(customerPortfolioMapper::toCustomerPortfolioResponse);
    }

    public Mono<TradeResponse> getStockTradeResponse(Integer customerId, TradeRequest tradeRequest) {
        return this.stockServiceClient.getStockPrice(tradeRequest.ticker())
                .map(stockPriceResponse -> customerPortfolioMapper.toStockTradeRequest(Integer.valueOf(stockPriceResponse.price()), tradeRequest))
                .flatMap(stockTradeRequest -> customerServiceClient.getStockTradeResponse(customerId, stockTradeRequest))
                .map(customerPortfolioMapper::toTradeResponse);
    }
}
