package com.reactive.microservice.aggregatorservice.client;

import com.reactive.microservice.aggregatorservice.dto.request.StockTradeRequest;
import com.reactive.microservice.aggregatorservice.dto.response.CustomerInformationResponse;
import com.reactive.microservice.aggregatorservice.dto.response.StockTradeResponse;
import com.reactive.microservice.aggregatorservice.exceptions.ApplicationException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class CustomerServiceClient {

    private final WebClient webClient;

    public CustomerServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /* *
     * GET http://localhost:6060/customers/{customerId}
     * */
    public Mono<CustomerInformationResponse> getCustomerPortfolio(Integer customerId) {
        return this.webClient.get()
                .uri("/customers/{customerId}", customerId)
                .retrieve()
                .bodyToMono(CustomerInformationResponse.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> ApplicationException.customerNotFound(customerId));
    }


    /* *
     * POST http://localhost:6060/customers/{customerId}/trade
     * */
    public Mono<StockTradeResponse> getStockTradeResponse(Integer customerId, StockTradeRequest stockTradeRequest) {
        return this.webClient.post()
                .uri("/customers/{customerId}/trade", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(stockTradeRequest)
                .retrieve()
                .bodyToMono(StockTradeResponse.class)
                .onErrorResume(WebClientResponseException.BadRequest.class, ApplicationException::handleBadRequest)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> ApplicationException.customerNotFound(customerId));
    }

}
