package com.reactive.microservice.aggregatorservice.config;

import com.reactive.microservice.aggregatorservice.client.CustomerServiceClient;
import com.reactive.microservice.aggregatorservice.client.StockServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public StockServiceClient stockServiceClient(@Value("${stock.service.url}") String baseUrl) {
        return new StockServiceClient(createWebClient(baseUrl));
    }

    @Bean
    public CustomerServiceClient customerServiceClient(@Value("${customer.service.url}") String baseUrl) {
        return new CustomerServiceClient(createWebClient(baseUrl));
    }

    private WebClient createWebClient(String baseUrl) {
        log.info("base url: {}", baseUrl);
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
