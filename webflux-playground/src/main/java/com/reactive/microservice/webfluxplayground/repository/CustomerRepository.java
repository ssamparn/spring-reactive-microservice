package com.reactive.microservice.webfluxplayground.repository;

import com.reactive.microservice.webfluxplayground.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    Flux<Customer> findByName(String name);

    Flux<Customer> findByEmailEndingWith(String email);

    @Modifying
    @Query("DELETE from customer WHERE id=:customerId")
    Mono<Boolean> deleteCustomerById(Integer customerId);

    Flux<Customer> findBy(Pageable pageable);

}
