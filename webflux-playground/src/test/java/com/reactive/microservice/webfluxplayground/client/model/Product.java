package com.reactive.microservice.webfluxplayground.client.model;

public record Product(Integer id,
                      String description,
                      Integer price) {
}
