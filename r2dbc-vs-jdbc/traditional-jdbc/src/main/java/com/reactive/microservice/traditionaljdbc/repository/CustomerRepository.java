package com.reactive.microservice.traditionaljdbc.repository;

import com.reactive.microservice.traditionaljdbc.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
