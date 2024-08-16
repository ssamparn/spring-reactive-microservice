package com.reactive.microservice.webfluxplayground.web.advice;

import com.reactive.microservice.webfluxplayground.exceptions.CustomerNotFoundException;
import com.reactive.microservice.webfluxplayground.exceptions.InvalidInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class FunctionalExceptionHandler {

    public Mono<ServerResponse> handleCustomerNotFoundException(CustomerNotFoundException ex, ServerRequest serverRequest) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("http://example.com/problems/customer-not-found"));
        problem.setTitle("Customer Not Found");
        problem.setInstance(URI.create(serverRequest.path()));
        return ServerResponse.status(HttpStatus.NOT_FOUND).body(Mono.just(problem), ProblemDetail.class);
    }

    public Mono<ServerResponse> handleInvalidInputException(InvalidInputException ex, ServerRequest serverRequest) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create("http://example.com/problems/invalid-input"));
        problem.setTitle("Invalid Input");
        problem.setInstance(URI.create(serverRequest.path()));
        return ServerResponse.status(HttpStatus.BAD_REQUEST).body(Mono.just(problem), ProblemDetail.class);

    }
}
