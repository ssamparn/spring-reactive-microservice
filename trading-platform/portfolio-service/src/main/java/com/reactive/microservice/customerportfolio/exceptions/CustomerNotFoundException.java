package com.reactive.microservice.customerportfolio.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Customer [id=%d] is not found";

    public CustomerNotFoundException(Integer customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
