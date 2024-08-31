package com.reactive.microservice.aggregatorservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
public class ApplicationException {

    public static <T> Mono<T> customerNotFound(Integer customerId) {
        return Mono.error(new CustomerNotFoundException(customerId));
    }

    public static <T> Mono<T> invalidTradeRequest(String message) {
        return Mono.error(new InvalidTradeRequestException(message));
    }

    public static <T> Mono<T> missingTicker() {
        return Mono.error(new InvalidTradeRequestException("Stock is required in the trade request!"));
    }

    public static <T> Mono<T> invalidStockQuantity() {
        return Mono.error(new InvalidTradeRequestException("Stock quantity should be greater than zero"));
    }

    public static <T> Mono<T> missingTradeAction() {
        return Mono.error(new InvalidTradeRequestException("Trade action is required in the trade request!"));
    }

    public static <T> Mono<T> handleBadRequest(WebClientResponseException.BadRequest exception) {
        ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
        String message = Objects.nonNull(problemDetail) ? problemDetail.getDetail() : exception.getMessage();
        log.error("customer service problem details: {}", problemDetail);
        return invalidTradeRequest(message);
    }
}
