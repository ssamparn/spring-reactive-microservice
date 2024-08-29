package com.reactive.microservice.aggregatorservice.model.response;

import com.reactive.microservice.aggregatorservice.domain.Ticker;
import com.reactive.microservice.aggregatorservice.domain.TradeAction;

/* *
 * Represents the customer trade response model object provided to the browser.
 * Formed from StockTradeResponse received from Customer Service
 * */

public record TradeResponse(Integer customerId,
                            Ticker ticker,
                            Integer price,
                            Integer quantity,
                            TradeAction traceAction,
                            Integer totalPrice,
                            Integer balance) {
}
