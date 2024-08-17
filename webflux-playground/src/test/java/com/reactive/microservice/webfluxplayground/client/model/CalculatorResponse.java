package com.reactive.microservice.webfluxplayground.client.model;

public record CalculatorResponse(Integer first,
                                 Integer second,
                                 String operation,
                                 Double result) {
}
