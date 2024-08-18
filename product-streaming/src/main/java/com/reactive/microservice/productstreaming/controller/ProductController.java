package com.reactive.microservice.productstreaming.controller;

import com.reactive.microservice.productstreaming.service.ProductService;
import com.reactive.microservice.productstreaming.model.ProductModel;
import com.reactive.microservice.productstreaming.model.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/* *
 * Streaming: We will be doing Service => Service communication
 * Let's consider a use case where we have to create a Million products.
 *
 * If we are to do it in a traditional way, there will be many disadvantages. Like
 *  - Increased network traffic / latency as we have make POST call to the api million times. And imagine if the service provider applied a rate limiting plan, then they will throttle our requests.
 *    We can not open 1000's of threads to create products in parallel.
 *  - Unnecessary waiting time
 *  - Redundant validation (JWT Token Validation or SSL Certificate Validation)
 *
 * To overcome this, there could be an option to upload a CSV file, but it will also have several issues.
 *  - File itself could be corrupt.
 *  - Since it is a "," separated file, there could be chance of a missing ",". If that happens then deserialization of file to java object would be impossible
 *  - An CSV file will not be able to handle complex nested data structure. For a payment application, it would be a lot more difficult.
 *
 * This problem can be easily solved with Streaming.
 *  - We will set up connection once (no need to make parallel requests as such) and keep sending the messages in a streaming fashion.
 *  - No need to wait for previous requests to complete.
 *  - Reduced network traffic / latency.
 *  - We can use JSON to create a product or item.
 *
 * But we can not use JSON Array Format in JSON Communication. We have to use JSON Line Format.
 * The problem with JSON Array Format is let's say there is a GET request to get all the 1000 products. The response (Json file) will start with a "[" bracket. e.g: [ {p1}, {p2}, {p3} .... {p1000}]
 * Let's imagine the server is writing data one by one. And after writing 500 products the server crashed. Now the client did see the opening " [ " bracket but did not see the closing " ] " bracket.
 * Since it has not received the closing " ] " bracket, it can not deserialize the complete data. The response is now basically corrupt. It's similar to all or nothing.
 * So the JSON Array is good for smaller data sets, but for larger datasets we have to use JSON Line Format.
 *
 * JSON Line Format:
 *  - aka ND-JSON : new-line delimited
 *  - Each line id 1 JSON object, which is self-contained, easier to parse (without getting out-of-memory exception), great for streaming and can easily handle massive data sets.
 *  - JSON Array is good for smaller and related data. e.g: Reviews for a product, but for larger data sets JSON Line Format is recommended.
 * */

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /* *
     * We are expecting a stream of product (json) objects to be uploaded as it is a bulk upload.
     * If you observe clearly, the endpoint is invoked once (that means the connection is established once), but products are being uploaded every second in a streaming fashion.
     * By using this technique, by invoking the post request only once, we can upload millions of products to the remote server
     * */
    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<UploadResponse> uploadProducts(@RequestBody Flux<ProductModel> productModelFlux) {
        log.info("upload product service invoked");
        return this.productService
                .saveProducts(productModelFlux) // .doOnNext(requestBody -> log.info("incoming request body: {}", requestBody)))
                .then(productService.getProductsCount())
                .map(productCount -> new UploadResponse(UUID.randomUUID(), productCount));
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ProductModel> downloadProducts() {
        log.info("download product service invoked");
        return this.productService.getProducts();
    }
}
