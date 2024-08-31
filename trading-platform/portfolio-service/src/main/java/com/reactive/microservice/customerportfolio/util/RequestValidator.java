package com.reactive.microservice.customerportfolio.util;

import com.reactive.microservice.customerportfolio.entity.Customer;
import com.reactive.microservice.customerportfolio.exceptions.ApplicationException;
import com.reactive.microservice.customerportfolio.model.request.StockTradeRequest;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RequestValidator {

    public static UnaryOperator<Mono<Customer>> validate(Integer customerId, StockTradeRequest request) {
        return customerMono -> customerMono
                .filter(hasEnoughBalance(request.quantity(), request.tickerPrice()))
                .switchIfEmpty(ApplicationException.insufficientBalance(customerId));
    }

    private static Predicate<Customer> hasEnoughBalance(Integer quantity, Integer tickerPrice) {
        return customer -> customer.getBalance() >= tickerPrice * quantity;
    }
}
