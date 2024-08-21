package com.reactive.microservice.customerportfolio.repository;

import com.reactive.microservice.customerportfolio.entity.PortfolioItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioItemRepository extends R2dbcRepository<PortfolioItem, Integer> {

}
