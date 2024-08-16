package com.reactive.microservice.webfluxplayground.web.router;

import com.reactive.microservice.webfluxplayground.exceptions.CustomerNotFoundException;
import com.reactive.microservice.webfluxplayground.exceptions.InvalidInputException;
import com.reactive.microservice.webfluxplayground.web.advice.FunctionalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> customerRoutes(RequestHandler requestHandler,
                                                         FunctionalExceptionHandler errorHandler) {
        return RouterFunctions.route()
                .GET(("/customers"), requestHandler::allCustomers)
                .GET(("/customers/paginated"), requestHandler::paginatedCustomers)
                .GET(("/customers/{customerId}"), requestHandler::getCustomer)
                .POST(("/customers"), requestHandler::saveCustomer)
                .PUT(("/customers/{customerId}"), requestHandler::updateCustomer)
                .DELETE(("/customers/{customerId}"), requestHandler::deleteCustomer)
                .onError(CustomerNotFoundException.class, errorHandler::handleCustomerNotFoundException)
                .onError(InvalidInputException.class, errorHandler::handleInvalidInputException)
                .build();
    }

    /* *
     * V Imp Note:
     *  1. We can have multiple beans of RouterFunction.
     *  2. Each bean of RouterFunction will have to have its own onError handler mappings.
     *  3. We can have nested router functions as well.
     * */
}
