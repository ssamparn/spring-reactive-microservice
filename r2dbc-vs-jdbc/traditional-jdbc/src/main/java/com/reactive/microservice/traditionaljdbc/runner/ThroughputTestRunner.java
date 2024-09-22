package com.reactive.microservice.traditionaljdbc.runner;

import com.reactive.microservice.traditionaljdbc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* *
 * Goal: Test Throughput: Number of tasks executed per unit time.
 * */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "throughput.test", havingValue = "true")
public class ThroughputTestRunner implements CommandLineRunner {

    private static final int TASKS_COUNT = 100_000;

    @Value("${useVirtualThreadExecutor:false}")
    private boolean useVirtualThreadExecutor;

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        log.info("Starting Traditional JDBC for Throughput Test !!");
        log.info("Is virtual thread executor?: {}", useVirtualThreadExecutor);

        // repeat the test 10 times. Ignore the test result of the first test run as that is for warm up
        for (int i = 1; i <= 10; i++) {
            ExecutorService executorService = useVirtualThreadExecutor ? getVirtualThreadExecutor() : getFixedThreadExecutor();
            this.measureTimeTaken(i, () -> this.runTest(executorService));
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
    private void runTest(ExecutorService executorService) {
        try (ExecutorService executor = executorService) {
            for (int i = 1; i < TASKS_COUNT; i++) {
                final Integer customerId = i;
                executor.submit(() -> this.customerRepository.findById(customerId));
            }
        } // we wait for all the tasks to complete
    }

    /* *
     * 256 number of threads configured in fixed thread pool to match the reactor thread context,
     * as reactor sends 256 at a time via flatMap. So this number is chosen to keep the test fair.
     * */
    private ExecutorService getFixedThreadExecutor() {
        return Executors.newFixedThreadPool(256);
    }

    /* *
     * Virtual thread executor does not have any internal queue. All the submitted tasks will be executed concurrently!
     * You might want to use semaphore to limit the concurrency if it is important!
     * */
    private ExecutorService getVirtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
