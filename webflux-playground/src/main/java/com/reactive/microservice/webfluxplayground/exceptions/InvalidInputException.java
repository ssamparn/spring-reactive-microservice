package com.reactive.microservice.webfluxplayground.exceptions;

public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }

}
