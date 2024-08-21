package com.reactive.microservice.customerportfolio.repository;

import com.reactive.microservice.customerportfolio.entity.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerPortfolioRepository extends R2dbcRepository<Customer, Integer> {

}
