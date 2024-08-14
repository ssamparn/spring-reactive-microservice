package com.reactive.microservice.webfluxplayground.model;

public record Product(Integer id,
                      String description,
                      Integer price) {
}
