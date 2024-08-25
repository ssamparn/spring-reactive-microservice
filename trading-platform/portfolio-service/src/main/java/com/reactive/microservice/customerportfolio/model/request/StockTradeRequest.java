package com.reactive.microservice.customerportfolio.model.request;

import com.reactive.microservice.customerportfolio.domain.Ticker;
import com.reactive.microservice.customerportfolio.domain.TradeAction;

public record StockTradeRequest(
        Ticker ticker,
        Integer tickerPrice,
        Integer quantity,
        TradeAction tradeAction) {

    public Integer totalPrice() {
        return tickerPrice * quantity;
    }
}
