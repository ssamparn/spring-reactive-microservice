package com.reactive.microservice.reactiver2dbc.repository;

import com.reactive.microservice.reactiver2dbc.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}
