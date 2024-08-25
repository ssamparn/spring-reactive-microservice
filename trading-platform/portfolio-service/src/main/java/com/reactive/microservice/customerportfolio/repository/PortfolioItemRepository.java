package com.reactive.microservice.customerportfolio.repository;

import com.reactive.microservice.customerportfolio.domain.Ticker;
import com.reactive.microservice.customerportfolio.entity.PortfolioItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PortfolioItemRepository extends R2dbcRepository<PortfolioItem, Integer> {

    Flux<PortfolioItem> findAllByCustomerId(Integer customerId);

    Mono<PortfolioItem> findByCustomerIdAndTicker(Integer customerId, Ticker ticker);

}
