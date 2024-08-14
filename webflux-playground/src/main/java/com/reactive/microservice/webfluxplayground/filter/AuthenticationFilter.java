package com.reactive.microservice.webfluxplayground.filter;

import com.reactive.microservice.webfluxplayground.model.CustomerCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.reactive.microservice.webfluxplayground.model.CustomerCategory.PRIME;
import static com.reactive.microservice.webfluxplayground.model.CustomerCategory.STANDARD;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/* *
 * WebFilter: Authentication Requirements!
 *   - We have 2 Types of Callers
 *          STANDARD, PRIME
 *   - All the callers are expected to provide a security header (x-auth-header) as part of requests.
 *   - The value should be
 *          "standard-secret" => STANDARD Caller
 *          "prime-secret" => PRIME Caller
 *   - Reject the unauthenticated requests with 401 status code
 * */

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements WebFilter {

    private static final String MANDATORY_HEADER = "x-auth-header";

    private static final Map<String, CustomerCategory> CUSTOMER_CATEGORY_MAP = Map.of(
            "standard-secret", STANDARD,
            "prime-secret", PRIME
    );

    private final FilterErrorHandler webFilterExceptionHandler;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("authentication filter");
        String authTokenValue = exchange.getRequest().getHeaders().getFirst(MANDATORY_HEADER); // authTokenValue can be either standard-secret or prime-secret
        if (isNotEmpty(authTokenValue) && CUSTOMER_CATEGORY_MAP.containsKey(authTokenValue)) {
            exchange.getAttributes().put("user-profile", CUSTOMER_CATEGORY_MAP.get(authTokenValue));
            return chain.filter(exchange);
        }

         return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
        // return webFilterExceptionHandler.sendProblemDetail(exchange, HttpStatus.UNAUTHORIZED, "Mandatory request header missing"); // Use this in case we want to return a problem detail object from webflux web filter.
    }
}
