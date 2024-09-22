package com.reactive.microservice.traditionaljdbc.runner;

import com.reactive.microservice.traditionaljdbc.entity.Customer;
import com.reactive.microservice.traditionaljdbc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/* *
 * Goal: Test resource efficiency: How much system resources a reactive r2dbc driver consumes.
 * */
@Slf4j
@Service
@ConditionalOnProperty(value = "efficiency.test", havingValue = "true")
@RequiredArgsConstructor
public class EfficiencyTestRunner implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        log.info("Starting Traditional JDBC for Efficiency Test !!");
        List<Customer> customers = this.customerRepository.findAll();
        log.info("Total number of customers: {}", customers.size());
        log.info("Done !!");
    }
}
