package com.reactive.microservice.webfluxplayground.service;

import com.reactive.microservice.webfluxplayground.entity.Customer;
import com.reactive.microservice.webfluxplayground.model.CustomerModel;
import com.reactive.microservice.webfluxplayground.repository.CustomerRepository;
import com.reactive.microservice.webfluxplayground.mapper.EntityModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Flux<CustomerModel> getAllCustomers() {
        return this.customerRepository.findAll()
                .map(EntityModelMapper::toModel);
    }

    public Flux<CustomerModel> getAllCustomers(Integer page, Integer size) {
        return this.customerRepository.findBy(PageRequest.of(page - 1, size)) // index starts with in spring data pageable interface (zero-indexed)
                .map(EntityModelMapper::toModel);
    }

    public Mono<CustomerModel> getCustomerById(Integer customerId) {
        return this.customerRepository.findById(customerId)
                .map(EntityModelMapper::toModel);
    }

    public Mono<CustomerModel> saveCustomer(Mono<CustomerModel> customerModelMono) {
        return customerModelMono
                .map(EntityModelMapper::toEntity)
                .flatMap(this.customerRepository::save)
                .map(EntityModelMapper::toModel);
    }

    public Mono<CustomerModel> updateCustomerWay1(Integer customerId, Mono<CustomerModel> customerModelMono) {
        return this.customerRepository.findById(customerId)
                .flatMap(customerEntity -> customerModelMono)
                .map(EntityModelMapper::toEntity)
                .doOnNext(customerEntity -> customerEntity.setId(customerId)) // this operation is safe and sequential
                .flatMap(this.customerRepository::save)
                .map(EntityModelMapper::toModel);
    }


    public Mono<CustomerModel> updateCustomerWay2(Integer customerId, Mono<CustomerModel> customerModelMono) {
        Mono<Customer> newCustomer = customerModelMono.map(EntityModelMapper::toEntity);

        return this.customerRepository.findById(customerId)
                .flatMap(customerEntity -> newCustomer)
                .doOnNext(customerEntity -> customerEntity.setId(customerId)) // this operation is safe and sequential
                .flatMap(this.customerRepository::save)
                .map(EntityModelMapper::toModel);
    }

    public Mono<Boolean> deleteCustomerById(Integer customerId) {
        return this.customerRepository.deleteCustomerById(customerId);
    }
}
