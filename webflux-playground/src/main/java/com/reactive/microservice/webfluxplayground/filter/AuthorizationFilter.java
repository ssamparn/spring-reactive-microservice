package com.reactive.microservice.webfluxplayground.filter;

import com.reactive.microservice.webfluxplayground.model.CustomerCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static com.reactive.microservice.webfluxplayground.model.CustomerCategory.STANDARD;

/* *
 * WebFilter: Authorization Requirements!
 *   - We have 2 Types of Callers
 *          STANDARD, PRIME
 *   - PRIME users are allowed to make any types of calls.
 *   - STANDARD users are allowed to make only GET calls.
 *   - Reject the unauthorized requests with 403 status code
 * */

@Slf4j
@Order(2)
@Component
public class AuthorizationFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("authorization filter");
        CustomerCategory customerCategory = exchange.getAttributeOrDefault("user-profile", STANDARD);

        return switch (customerCategory) {
            case STANDARD -> standard(exchange, chain);
            case PRIME ->  prime(exchange, chain);
        };
    }

    public Mono<Void> prime(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange);
    }

    private Mono<Void> standard(ServerWebExchange exchange, WebFilterChain chain) {
       if (exchange.getRequest().getMethod().matches(HttpMethod.GET.name())) {
           return chain.filter(exchange);
       }
       return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
    }
}
