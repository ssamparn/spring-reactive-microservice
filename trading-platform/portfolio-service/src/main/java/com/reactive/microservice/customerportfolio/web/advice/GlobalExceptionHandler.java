package com.reactive.microservice.customerportfolio.web.advice;

import com.reactive.microservice.customerportfolio.exceptions.CustomerNotFoundException;
import com.reactive.microservice.customerportfolio.exceptions.InsufficientBalanceException;
import com.reactive.microservice.customerportfolio.exceptions.InsufficientSharesException;
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

    @ExceptionHandler(InsufficientBalanceException.class)
    protected ProblemDetail handleInsufficientBalance(InsufficientBalanceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Insufficient balance to execute the trade !!");
        problemDetail.setType(URI.create("https://example.com/problems/insufficient-balance"));
        problemDetail.setProperty("errors", List.of(ErrorDetails.INSUFFICIENT_BALANCE));
        return problemDetail;
    }

    @ExceptionHandler(InsufficientSharesException.class)
    protected ProblemDetail handleInsufficientShares(InsufficientSharesException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Insufficient shares to execute the trade !!");
        problemDetail.setType(URI.create("https://example.com/problems/insufficient-shares"));
        problemDetail.setProperty("errors", List.of(ErrorDetails.INSUFFICIENT_SHARES));
        return problemDetail;
    }
}
