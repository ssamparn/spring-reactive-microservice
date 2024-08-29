package com.reactive.microservice.aggregatorservice.model.response;

import com.reactive.microservice.aggregatorservice.domain.Ticker;

import java.time.LocalDateTime;

public record StockPriceStream(Ticker ticker,
                               String price,
                               LocalDateTime time) {
}
