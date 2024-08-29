package com.reactive.microservice.aggregatorservice.model.request;

import com.reactive.microservice.aggregatorservice.domain.Ticker;
import com.reactive.microservice.aggregatorservice.domain.TradeAction;

/* *
 * Represents the customer trade request model object received from the browser.
 * Will be used to form StockTradeRequest (ticker, tickerPrice, quantity, tradeAction) to make the trade.
 * Only the ticker price is missing which will be received from stock service.
 * */
public record TradeRequest(Ticker ticker,
                           Integer quantity,
                           TradeAction tradeAction) {
}
