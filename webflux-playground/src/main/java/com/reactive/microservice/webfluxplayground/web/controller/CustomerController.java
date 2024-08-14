package com.reactive.microservice.webfluxplayground.web.controller;

import com.reactive.microservice.webfluxplayground.exceptions.ApplicationExceptions;
import com.reactive.microservice.webfluxplayground.model.CustomerModel;
import com.reactive.microservice.webfluxplayground.service.CustomerService;
import com.reactive.microservice.webfluxplayground.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Flux<CustomerModel> allCustomers() {
        return this.customerService.getAllCustomers();
    }

    @GetMapping("/paginated")
    public Mono<List<CustomerModel>> allCustomers(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "3") Integer size) {
        return this.customerService.getAllCustomers(page, size)
                                    .collectList();
    }

    @GetMapping("/{customerId}")
    public Mono<CustomerModel> getCustomer(@PathVariable Integer customerId) {
        return this.customerService.getCustomerById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId));
    }

    @PostMapping
    public Mono<CustomerModel> saveCustomer(@RequestBody Mono<CustomerModel> customerModelMono) {
        return customerModelMono.transform(RequestValidator.validate())
                .as(this.customerService::saveCustomer);
    }

    @PutMapping("/{customerId}")
    public Mono<CustomerModel> updateCustomer(@PathVariable Integer customerId, @RequestBody Mono<CustomerModel> customerModelMono) {
        return customerModelMono.transform(RequestValidator.validate())
                .as(validatedCustomer -> this.customerService.updateCustomerWay2(customerId, validatedCustomer))
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId));
    }

    @DeleteMapping("/{customerId}")
    public Mono<Void> deleteCustomer(@PathVariable Integer customerId) {
        return this.customerService.deleteCustomerById(customerId)
                .filter(deleted -> deleted)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .then();
    }

}
