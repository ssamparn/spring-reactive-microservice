package com.reactive.microservice.aggregatorservice.mapper;

import com.reactive.microservice.aggregatorservice.dto.request.StockTradeRequest;
import com.reactive.microservice.aggregatorservice.dto.response.CustomerInformationResponse;
import com.reactive.microservice.aggregatorservice.dto.response.StockTradeResponse;
import com.reactive.microservice.aggregatorservice.model.request.TradeRequest;
import com.reactive.microservice.aggregatorservice.model.response.CustomerPortfolioResponse;
import com.reactive.microservice.aggregatorservice.model.response.TradeResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerPortfolioMapper {

    public CustomerPortfolioResponse toCustomerPortfolioResponse(CustomerInformationResponse customerInformationResponse) {
        CustomerPortfolioResponse customerPortfolioResponse = new CustomerPortfolioResponse(
                customerInformationResponse.customerId(),
                customerInformationResponse.customerName(),
                customerInformationResponse.balance(),
                customerInformationResponse.holdings()
        );
        return customerPortfolioResponse;
    }

    public StockTradeRequest toStockTradeRequest(Integer tickerPrice, TradeRequest tradeRequest) {
        StockTradeRequest stockTradeRequest = new StockTradeRequest(
                tradeRequest.ticker(),
                tickerPrice,
                tradeRequest.quantity(),
                tradeRequest.tradeAction()
        );
        return stockTradeRequest;
    }

    public TradeResponse toTradeResponse(StockTradeResponse stockTradeResponse) {
        TradeResponse tradeResponse = new TradeResponse(
            stockTradeResponse.customerId(),
            stockTradeResponse.ticker(),
                stockTradeResponse.tickerPrice(),
                stockTradeResponse.quantity(),
                stockTradeResponse.tradeAction(),
                stockTradeResponse.totalPrice(),
                stockTradeResponse.balance()
        );
        return tradeResponse;
    }
}
