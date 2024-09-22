package com.reactive.microservice.reactiver2dbc.runner;

import com.reactive.microservice.reactiver2dbc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

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
        log.info("Starting Reactive R2DBC for Efficiency Test !!");
        AtomicInteger atomicInteger = new AtomicInteger(0);
        this.customerRepository.findAll()
                .doOnNext(customer -> {
                    int count = atomicInteger.incrementAndGet(); // for every customer, increment
                    if (count % 1_000_000 == 0) {
                        log.info("customers: {}", count); // print for every 1 million
                    }
                })
                .then()
                .block();
        log.info("Done !!");
    }
}
