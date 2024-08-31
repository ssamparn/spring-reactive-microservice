package com.reactive.microservice.productstreaming.service;

import com.reactive.microservice.productstreaming.client.ProductStreamClient;
import com.reactive.microservice.productstreaming.model.ProductModel;
import com.reactive.microservice.productstreaming.util.FileWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import static com.reactive.microservice.productstreaming.util.TestUtil.onNext;

/**
 * Configured to run test with complete spring context and against a defined port 8080.
 * This is to ensure mvn build runs successfully.
 * But for request streaming demo, start the application and run tests manually
 * */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ProductUploadDownloadTest {

    private final ProductStreamClient productStreamClient = new ProductStreamClient();

    @Test
    public void uploadSingleProductTest() {
        // Creating Product Request Flux just for Demo
        // delay is intentional to demo product upload endpoint will be invoked immediately, but product will start uploading after 5 seconds
        Flux<ProductModel> productModelFlux = Flux.just(new ProductModel(null, "iPhone", ThreadLocalRandom.current().nextInt()))
                        .delayElements(Duration.ofSeconds(5));

        productStreamClient
                .uploadProducts(productModelFlux)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    public void uploadMultipleProductTest() {
        // Creating Product Request Flux just for Demo
        // delay is intentional to demo product upload endpoint will be invoked immediately and once, but each product will be uploaded with a delay of 1 second.
        // See the log
        Flux<ProductModel> productModelFlux = Flux.range(1, 10)
                .map(i -> new ProductModel(null, "product-" + i, ThreadLocalRandom.current().nextInt()))
                .delayElements(Duration.ofSeconds(1));

        productStreamClient
                .uploadProducts(productModelFlux)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    /**
     * 1 Million products upload - Demo
     * */
    @Test
    public void uploadMillionProductTest() {
        Flux<ProductModel> productModelFlux = Flux.range(1, 1_000_000)
                .map(i -> new ProductModel(null, "product-" + i, ThreadLocalRandom.current().nextInt()));

        productStreamClient
                .uploadProducts(productModelFlux)
                .doOnNext(onNext())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    /* *
     * Download all products and write it to a file
     * */
    @Test
    public void downloadMillionProductTest() {
        Flux<ProductModel> productModelFlux = Flux.range(1, 1_000_000)
                .map(i -> new ProductModel(null, "product-" + i, ThreadLocalRandom.current().nextInt()));

        /* *
         * Since spring creates a new context each time a test starts, it writes only default number of products to the file.
         * If we have to download all 1 million products and write those to the file, we need to first upload all 1 million and then download in one spring app context.
         * So before downloading all 1 million products, we have to upload all 1 million products.
         */
        productStreamClient
                .uploadProducts(productModelFlux)
                .doOnNext(onNext())
                .thenMany(productStreamClient.downloadProducts())
                .map(Record::toString)
                .as(content -> FileWriter.create(content, Path.of("src/test/resources/products.txt")))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}
