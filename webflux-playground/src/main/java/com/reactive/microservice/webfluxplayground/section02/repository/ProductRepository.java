package com.reactive.microservice.webfluxplayground.section02.repository;

import com.reactive.microservice.webfluxplayground.section02.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/* *
 * Reference: https://docs.spring.io/spring-data/relational/reference/r2dbc/query-methods.html
 *
 * */

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {

    // from <= product price <= to
    Flux<Product> findByPriceBetween(int fromPrice, int toPrice);

    /* *
     * Spring Data r2dbc supports paginated result to support requests for chunks of data from larger data set.
     * */

    // Assignment: Page 1, Size 10. Sort by Price ascending
    // Remember page index starts with 0 in Pageable interface
    Flux<Product> findAllBy(Pageable pageable);
}
