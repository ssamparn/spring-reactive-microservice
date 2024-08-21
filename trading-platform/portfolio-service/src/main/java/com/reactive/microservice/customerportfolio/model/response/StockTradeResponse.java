package com.reactive.microservice.customerportfolio.model.response;

import com.reactive.microservice.customerportfolio.domain.Ticker;
import com.reactive.microservice.customerportfolio.domain.TradeAction;

public record StockTradeResponse(
        Integer customerId,
        Ticker ticker,
        Integer tickerPrice,
        Integer quantity,
        TradeAction tradeAction,
        Integer totalPrice,
        Integer balance) {

}
