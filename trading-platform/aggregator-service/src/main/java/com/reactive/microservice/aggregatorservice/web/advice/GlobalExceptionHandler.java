package com.reactive.microservice.aggregatorservice.web.advice;

import com.reactive.microservice.aggregatorservice.exceptions.CustomerNotFoundException;
import com.reactive.microservice.aggregatorservice.exceptions.InvalidTradeRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;

/* *
 * reference: https://www.baeldung.com/spring-boot-custom-webflux-exceptions
 * */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    protected ProblemDetail handleCustomerNotFound(CustomerNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Customer not found !!");
        problemDetail.setType(URI.create("https://example.com/problems/user-not-found"));
        problemDetail.setProperty("errors", List.of(ErrorDetails.CUSTOMER_NOT_FOUND));
        return problemDetail;
    }

    @ExceptionHandler(InvalidTradeRequestException.class)
    protected ProblemDetail handleInvalidTradeRequest(InvalidTradeRequestException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid trade request !!");
        problemDetail.setType(URI.create("https://example.com/problems/invalid-trade-request"));
        problemDetail.setProperty("errors", List.of(ErrorDetails.INVALID_TRADE_REQUEST));
        return problemDetail;
    }
}
