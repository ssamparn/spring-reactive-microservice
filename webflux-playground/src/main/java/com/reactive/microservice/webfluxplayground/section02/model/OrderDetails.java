package com.reactive.microservice.webfluxplayground.section02.model;

import java.time.Instant;
import java.util.UUID;

// Represents r2dbc projection
public record OrderDetails(UUID orderId,
                           String customerName,
                           String productName,
                           Integer amount,
                           Instant orderDate) {
}
