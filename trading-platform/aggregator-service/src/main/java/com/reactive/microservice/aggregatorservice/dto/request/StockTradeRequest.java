package com.reactive.microservice.aggregatorservice.dto.request;

import com.reactive.microservice.aggregatorservice.domain.Ticker;
import com.reactive.microservice.aggregatorservice.domain.TradeAction;

/* *
 * Represents the request body while making POST call to Customer Portfolio Service to execute the trade (BUY / SELL transaction)
 * */
public record StockTradeRequest(
        Ticker ticker,
        Integer tickerPrice,
        Integer quantity,
        TradeAction tradeAction) {

}
