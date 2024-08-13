package com.reactive.microservice.webfluxplayground.section02;

import com.reactive.microservice.webfluxplayground.section02.entity.Customer;
import com.reactive.microservice.webfluxplayground.section02.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@Slf4j
public class CustomerRepositoryTest extends AbstractTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void findAll() {
        this.customerRepository.findAll()
                       .doOnNext(c -> log.info("customer received: {}", c))
                       .as(StepVerifier::create)
                       .expectNextCount(10)
                       .expectComplete()
                       .verify();
    }

    @Test
    public void findById() {
        this.customerRepository.findById(2)
                       .doOnNext(c -> log.info("customer received: {}", c))
                       .as(StepVerifier::create)
                       .assertNext(c -> Assertions.assertEquals("mike", c.getName()))
                       .expectComplete()
                       .verify();
    }

    @Test
    public void findByName() {
        this.customerRepository.findByName("jake")
                       .doOnNext(c -> log.info("customer received: {}", c))
                       .as(StepVerifier::create)
                       .assertNext(c -> Assertions.assertEquals("jake@gmail.com", c.getEmail()))
                       .expectComplete()
                       .verify();
    }

    /* *
     * Query methods:
     * https://docs.spring.io/spring-data/relational/reference/r2dbc/query-methods.html
     * */

    @Test
    public void findByEmailEndingWith() {
        this.customerRepository.findByEmailEndingWith("ke@gmail.com")
                       .doOnNext(c -> log.info("customer received: {}", c))
                       .as(StepVerifier::create)
                       .assertNext(c -> Assertions.assertEquals("mike@gmail.com", c.getEmail()))
                       .assertNext(c -> Assertions.assertEquals("jake@gmail.com", c.getEmail()))
                       .expectComplete()
                       .verify();
    }

    @Test
    public void insertAndDeleteCustomer() {
        // insert
        Customer customer = new Customer();
        customer.setName("marshal");
        customer.setEmail("marshal@gmail.com");
        this.customerRepository.save(customer)
                       .doOnNext(c -> log.info("customer received: {}", c))
                       .as(StepVerifier::create)
                       .assertNext(c -> Assertions.assertNotNull(c.getId()))
                       .expectComplete()
                       .verify();
        // count
        this.customerRepository.count()
                       .as(StepVerifier::create)
                       .expectNext(11L)
                       .expectComplete()
                       .verify();
        // delete
        this.customerRepository.deleteById(11)
                       .then(this.customerRepository.count())
                       .as(StepVerifier::create)
                       .expectNext(10L)
                       .expectComplete()
                       .verify();
    }

    /* *
     * Mutating Objects:
     * Non-Blocking IO. doOnNext() is for mutating the objects! Your entity objects are mutable!
     * Reactive operators while mutating an item are not invoked concurrently. It is sequential.
     *
     * this.repository.findById(id)
     *              .doOnNext(c -> c.setName("name"));
     *
     * is equivalent to below traditional style
     *
     * Customer customer = new Customer();
     * customer.setName("name");
     * */
    @Test
    public void updateCustomer() {
        this.customerRepository.findByName("ethan")
                       .doOnNext(c -> c.setName("noel")) // It is for mutating!
                       .flatMap(c -> this.customerRepository.save(c))
                       .doOnNext(c -> log.info("customer received: {}", c))
                       .as(StepVerifier::create)
                       .assertNext(c -> Assertions.assertEquals("noel", c.getName()))
                       .expectComplete()
                       .verify();
    }

}
