package com.reactive.microservice.webfluxplayground;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

@Slf4j
public abstract class AbstractWebClient {

    protected WebClient createWebClient() {
        return createWebClient(WebClient.Builder::build);
    }

    protected WebClient createWebClient(Consumer<WebClient.Builder> webClientConsumer) {
        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl("http://localhost:7070/demo02"); // demo 02 has all the endpoints to be used for web client.

        webClientConsumer.accept(builder);

        return builder.build();
    }
}