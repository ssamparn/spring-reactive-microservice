package com.reactive.microservice.webfluxplayground.mapper;

import com.reactive.microservice.webfluxplayground.entity.Customer;
import com.reactive.microservice.webfluxplayground.model.CustomerModel;

public class EntityModelMapper {

    public static Customer toEntity(CustomerModel model){
        var customer = new Customer();
        customer.setName(model.name());
        customer.setEmail(model.email());
        customer.setId(model.id());
        return customer;
    }

    public static CustomerModel toModel(Customer customer){
        return new CustomerModel(
                customer.getId(),
                customer.getName(),
                customer.getEmail()
        );
    }

}
