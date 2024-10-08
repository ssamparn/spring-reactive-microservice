package com.reactive.microservice.webfluxplayground.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Customer [id=%d] is not found";

    public CustomerNotFoundException(Integer customerId) {
        super(MESSAGE.formatted(customerId));
    }

}
