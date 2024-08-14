package com.reactive.microservice.webfluxplayground.validator;

import com.reactive.microservice.webfluxplayground.exceptions.ApplicationExceptions;
import com.reactive.microservice.webfluxplayground.model.CustomerModel;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class RequestValidator {

    private static Predicate<CustomerModel> hasName() {
        return customer -> StringUtils.isNotEmpty(customer.name());
    }

    private static Predicate<CustomerModel> hasValidEmail() {
        return customer -> !Objects.isNull(customer.email()) && Pattern.compile("^(.+)@(\\S+)$").matcher(customer.email()).matches();
    }

    public static UnaryOperator<Mono<CustomerModel>> validate() {
        return customerMono -> customerMono
                .filter(hasName())
                .switchIfEmpty(ApplicationExceptions.missingName())
                .filter(hasValidEmail())
                .switchIfEmpty(ApplicationExceptions.missingValidEmail());
    }
}
