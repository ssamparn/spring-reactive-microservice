package com.reactive.microservice.aggregatorservice.model.response;

import com.reactive.microservice.aggregatorservice.domain.Ticker;

import java.time.LocalDateTime;

/* *
 * 1. Represents updated stock price stream response coming from Stock service price stream.
 * 2. Represents the Stock price stream model provided to the browser.
 * */
public record StockPriceStream(Ticker ticker,
                               String price,
                               LocalDateTime time) {
}
