package com.reactive.microservice.aggregatorservice.dto.response;

import com.reactive.microservice.aggregatorservice.domain.Ticker;
import com.reactive.microservice.aggregatorservice.domain.TradeAction;

/* *
 * Represents the response body for POST call to Customer Portfolio Service after executing the trade (BUY / SELL transaction)
 * */
public record StockTradeResponse(
        Integer customerId,
        Ticker ticker,
        Integer tickerPrice,
        Integer quantity,
        TradeAction tradeAction,
        Integer totalPrice,
        Integer balance) {

}
