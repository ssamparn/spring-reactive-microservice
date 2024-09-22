package com.reactive.microservice.reactiver2dbc.runner;

import com.reactive.microservice.reactiver2dbc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/* *
 * Goal: Test Throughput: Number of tasks executed per unit time.
 * */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "throughput.test", havingValue = "true")
public class ThroughputTestRunner implements CommandLineRunner {

    private static final int TASKS_COUNT = 100_000;

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        log.info("Starting Reactive R2DBC for Throughput Test !!");

        // repeat the test 10 times. Ignore the test result of the first test run as that is for warm up

        for (int i = 1; i <= 10; i++) {
            this.measureTimeTaken(i, this::runTest);
        }

        log.info("Done !!");
    }

    private void measureTimeTaken(int iteration, Runnable taskRunnable) {
        long start = System.currentTimeMillis();
        taskRunnable.run();
        long timeTaken = (System.currentTimeMillis() - start); // throughput in millis
        var throughput = (1.0 * TASKS_COUNT / timeTaken) * 1000; // we multiply by 1000 to get throughput in seconds.

        log.info("test: {} - took: {} ms, throughput: {} / sec", iteration, timeTaken, throughput);
    }

    /* *
     * Make TASKS_COUNT number of queries to the database.
     * Each call is for fetching 1 single customer information.
     * Customer Details are already pre-populated into the database.
     * */
    private void runTest() {
        Flux.range(1, TASKS_COUNT)
                .flatMap(this.customerRepository::findById)
                .then()
                .block(); // we wait for all the tasks to complete
    }
}
