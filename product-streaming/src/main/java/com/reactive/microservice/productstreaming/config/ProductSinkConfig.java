package com.reactive.microservice.productstreaming.config;

import com.reactive.microservice.productstreaming.model.ProductModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class ProductSinkConfig {

    @Bean
    public Sinks.Many<ProductModel> productModelSink() {
        return Sinks.many().multicast().directAllOrNothing();
    }
}
