package com.reactive.microservice.webfluxplayground.section02.repository;

import com.reactive.microservice.webfluxplayground.section02.entity.CustomerOrder;
import com.reactive.microservice.webfluxplayground.section02.entity.Product;
import com.reactive.microservice.webfluxplayground.section02.model.OrderDetails;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CustomerOrderRepository extends ReactiveCrudRepository<CustomerOrder, UUID> {

    /* *
     * Complex Queries / Join:
     *  As we know r2dbc does not support @OneToMany or @ManyToMany JPA annotations. So how do we perform Complex Queries or Join operations.
     *  r2dbc tries to focus on performance and scalability that's why it is simple.
     *
     *  So for complex operations, prefer SQL. It is efficient and no N + 1 problem.
     * */

    // find all the products ordered by the customer. e.g: Find the products ordered by Sam
    @Query("""
        SELECT 
            p.* 
        FROM 
            customer c
        INNER JOIN customer_order co ON c.id = co.customer_id
        INNER JOIN product p ON co.product_id = p.id
        WHERE 
            c.name = :customerName
    """)
    Flux<Product> getProductsOrderedByCustomer(String customerName);

    /* *
     * Fetching Multiple fields across different tables, based on a given data.
     * Each row provides a combined view from multiple fields of multiple tables.
     * In SQL terminologies, we call that "Projection"
     *
     * */
    // find OrderId, Customer Name, Product Name, Purchase amount, Order Date based on given Product Name
    @Query("""
        SELECT
            co.order_id,
            c.name AS customer_name,
            p.description AS product_name,
            co.amount,
            co.order_date
        FROM
            customer c
        INNER JOIN customer_order co ON c.id = co.customer_id
        INNER JOIN product p ON co.product_id = p.id
        WHERE
            p.description = :productDescription
        ORDER BY co.amount DESC
        """)
    Flux<OrderDetails> getOrderDetailsByProduct(String productDescription);
}
