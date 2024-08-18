package com.reactive.microservice.productstreaming.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Table(name = "product")
public class Product {

    @Id
    private Integer id;
    private String description;
    private Integer price;
}
