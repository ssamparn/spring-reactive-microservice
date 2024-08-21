package com.reactive.microservice.customerportfolio.model;

import com.reactive.microservice.customerportfolio.domain.Ticker;

public record Holding(
        Ticker ticker,
        Integer quantity) {
}
