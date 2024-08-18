package com.reactive.microservice.productstreaming.mapper;

import com.reactive.microservice.productstreaming.entity.Product;
import com.reactive.microservice.productstreaming.model.ProductModel;

public class EntityModelMapper {

    public static Product toEntity(ProductModel model) {
        Product product = new Product();
        product.setId(model.id());
        product.setDescription(model.description());
        product.setPrice(model.price());
        return product;
    }

    public static ProductModel toModel(Product product) {
        return new ProductModel(
                product.getId(),
                product.getDescription(),
                product.getPrice()
        );
    }

}
