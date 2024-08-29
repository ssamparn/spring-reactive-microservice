package com.reactive.microservice.aggregatorservice.dto.response;

import com.reactive.microservice.aggregatorservice.domain.Ticker;

/* *
 * Represents the response body for GET call to Stock Service to fetch individual stock price.
 * */
public record StockPriceResponse(Ticker ticker,
                                 String price) {
}
