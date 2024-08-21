package com.reactive.microservice.customerportfolio.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
public class Customer {

    @Id
    private Integer id;
    private String customerName;
    private Integer balance;
}
