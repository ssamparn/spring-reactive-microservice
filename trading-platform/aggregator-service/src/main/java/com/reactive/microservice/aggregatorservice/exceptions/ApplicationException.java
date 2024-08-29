package com.reactive.microservice.aggregatorservice.exceptions;

import reactor.core.publisher.Mono;

public class ApplicationException {

    public static <T> Mono<T> customerNotFound(Integer customerId) {
        return Mono.error(new CustomerNotFoundException(customerId));
    }
}
