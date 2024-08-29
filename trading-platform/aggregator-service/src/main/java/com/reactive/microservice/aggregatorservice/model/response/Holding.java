package com.reactive.microservice.aggregatorservice.model.response;

import com.reactive.microservice.aggregatorservice.domain.Ticker;

public record Holding(Ticker ticker,
                      Integer quantity) {
}
