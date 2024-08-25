package com.reactive.microservice.customerportfolio.service;

import com.reactive.microservice.customerportfolio.entity.Customer;
import com.reactive.microservice.customerportfolio.entity.PortfolioItem;
import com.reactive.microservice.customerportfolio.exceptions.ApplicationException;
import com.reactive.microservice.customerportfolio.mapper.EntityModelMapper;
import com.reactive.microservice.customerportfolio.model.Holding;
import com.reactive.microservice.customerportfolio.model.response.CustomerInformationResponse;
import com.reactive.microservice.customerportfolio.repository.CustomerPortfolioRepository;
import com.reactive.microservice.customerportfolio.repository.PortfolioItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerPortfolioService {

    private final CustomerPortfolioRepository customerPortfolioRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final EntityModelMapper entityModelMapper;

    /* *
     * Approach 1: Plain and Simple Approach
     * */
    public Mono<CustomerInformationResponse> getCustomerInformationApproach1(Integer customerId) {
        return this.customerPortfolioRepository.findById(customerId)
                .switchIfEmpty(ApplicationException.customerNotFound(customerId))
                .flatMap(this::buildCustomerInformationResponse);
    }

    private Mono<CustomerInformationResponse> buildCustomerInformationResponse(Customer customerEntity) {
        return this.portfolioItemRepository.findAllByCustomerId(customerEntity.getId())
                .collectList()
                .map(portfolioItems -> this.entityModelMapper.toCustomerInformationResponse(customerEntity, portfolioItems));
    }


    /* *
     * Approach 2: Mono.zip() approach
     * */
    public Mono<CustomerInformationResponse> getCustomerInformationApproach2(Integer customerId) {
        Mono<Customer> customerMono = this.customerPortfolioRepository.findById(customerId)
                .switchIfEmpty(ApplicationException.customerNotFound(customerId));

        Flux<PortfolioItem> portfolioItemFlux = this.portfolioItemRepository.findAllByCustomerId(customerId);

        return Mono.zip(customerMono, portfolioItemFlux.collectList(),
                (customer, portfolioItems) -> new CustomerInformationResponse(
                        customer.getId(),
                        customer.getCustomerName(),
                        customer.getBalance(),
                        portfolioItems.stream()
                                .map(portfolioItem -> new Holding(portfolioItem.getTicker(), portfolioItem.getQuantity()))
                                .toList()));
    }
}
