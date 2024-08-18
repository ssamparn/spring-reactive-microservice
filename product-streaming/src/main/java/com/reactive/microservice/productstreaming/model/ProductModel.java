package com.reactive.microservice.productstreaming.model;

public record ProductModel(Integer id,
                           String description,
                           Integer price) {
}
