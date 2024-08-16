package com.reactive.microservice.webfluxplayground.web.router;

import com.reactive.microservice.webfluxplayground.exceptions.ApplicationExceptions;
import com.reactive.microservice.webfluxplayground.model.CustomerModel;
import com.reactive.microservice.webfluxplayground.service.CustomerService;
import com.reactive.microservice.webfluxplayground.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RequestHandler {

    private final CustomerService customerService;

    public Mono<ServerResponse> allCustomers(ServerRequest serverRequest) {
        return ServerResponse.ok().body(this.customerService.getAllCustomers(), CustomerModel.class);
    }

    public Mono<ServerResponse> paginatedCustomers(ServerRequest serverRequest) {
        Integer page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(1);
        Integer size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(3);
        return this.customerService.getAllCustomers(page, size)
                .collectList()
                .flatMap(customerModel -> ServerResponse.ok().body(Mono.just(customerModel), CustomerModel.class));
    }

    public Mono<ServerResponse> getCustomer(ServerRequest serverRequest) {
        Integer customerId = Integer.parseInt(serverRequest.pathVariable("customerId"));
        return this.customerService.getCustomerById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .flatMap(customerModel -> ServerResponse.ok().body(Mono.just(customerModel), CustomerModel.class));
    }


    public Mono<ServerResponse> saveCustomer(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CustomerModel.class)
                .transform(RequestValidator.validate())
                .as(this.customerService::saveCustomer)
                .flatMap(customerModel -> ServerResponse.ok().body(Mono.just(customerModel), CustomerModel.class));

    }

    public Mono<ServerResponse> updateCustomer(ServerRequest serverRequest) {
        Integer customerId = Integer.parseInt(serverRequest.pathVariable("customerId"));

        return serverRequest.bodyToMono(CustomerModel.class)
                .transform(RequestValidator.validate())
                .as(validatedCustomerModel -> customerService.updateCustomerWay1(customerId, validatedCustomerModel))
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .flatMap(customerModel -> ServerResponse.ok().body(Mono.just(customerModel), CustomerModel.class));
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest serverRequest) {
        Integer customerId = Integer.parseInt(serverRequest.pathVariable("customerId"));

        return this.customerService.deleteCustomerById(customerId)
                .filter(deleted -> deleted)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .then(ServerResponse.ok().build());
    }
}
