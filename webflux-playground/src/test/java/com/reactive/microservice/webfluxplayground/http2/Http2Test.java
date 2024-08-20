package com.reactive.microservice.webfluxplayground.http2;

import com.reactive.microservice.webfluxplayground.AbstractWebClient;
import com.reactive.microservice.webfluxplayground.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.test.StepVerifier;

/* *
 * HTTP2 Introduction:
 *  As we saw in the HttpConnectionPooling Test, Http v1.1 requires one connection per request. So we need too many connections to process multiple concurrent requests.
 *  So to overcome this Google invented Http v2 in 2015. Http2 does not need connections per requests. In fact, it just needs one connection & with that one connection, it can send multiple concurrent requests.
 *  So it does not need a lot of system resources.
 *
 *  Features:
 *      1. Multiplexing: Ability to send multiple concurrent requests with just a single connection is called multiplexing.
 *      2. Binary Protocol: Http2 is Binary protocol whereas Http1 is textual.
 *      3. Header Compression: Http2 enables header compression techniques to reduce the size of requests and response headers leading to faster transmission times.
 *
 * Note: 1. Our service application (server) should be able to support both Http v1.1 and Http2. The server can support Http2 by enabling a property. server.http2.enabled=true
 *       2. Client should send which protocol it wants to use!
 *       3. We might not see the benefit of Http2 if we don't have many concurrent requests.
 *       4. Ensure that your load balancer supports Http2.
 * */

@Slf4j
public class Http2Test extends AbstractWebClient {

    Integer poolSize = 1; // We don't need multiple connections as we are enabling Http2 for the client.
    ConnectionProvider provider = ConnectionProvider.builder("sassaman")
            .lifo() // LIFO: Last In First Out ; FIFO: First In First Out. It is recommended to use lifo, as it will try to close old connections.
            .maxConnections(poolSize)
            .build();
    HttpClient httpClient = HttpClient.create(provider)
            .protocol(HttpProtocol.H2C) // H2C: Enables Http2 as long as the connection is not SSL encrypted. For SSL encrypted connections, use H2 to enable Http2.
            .compress(true)
            .keepAlive(true);

    private final WebClient client = WebClient.builder()
            .baseUrl("http://localhost:7070/demo03")
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();

    /**
     * Concurrent Requests Demo:
     *   Change the value from 1 to multiple e.g. 10. Observe the network logs of the external-service (in this case swagger endpoint) via netstat command to observe 2 things.
     *      1. See how for each request, NO new connection to external-service is being established and that's because of HTTP2.
     *      2. See how for each request an outbound port from the host (client machine) is being opened.
     * */
    @Test
    public void concurrentRequests() {
        Integer maxRequests = 100; //
        Flux.range(1, maxRequests)
                .flatMap(this::getProduct) // V. Imp Note: flatMap() internally subscribes to the publisher in parallel
                .collectList()
                .as(StepVerifier::create)
                .assertNext(l -> Assertions.assertEquals(maxRequests, l.size()))
                .expectComplete()
                .verify();
    }

    /* *
     * Product Service: Provides the product name for the given product id (1 to 20,000). It is a slow service. Response time will take up to 5 seconds.
     * GET http://localhost:7070/demo03/product/{productId}
     * */
    private Mono<Product> getProduct(int productId) {
        return this.client.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono(Product.class);
    }
}