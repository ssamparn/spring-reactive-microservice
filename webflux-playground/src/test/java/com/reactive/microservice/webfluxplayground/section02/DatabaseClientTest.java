package com.reactive.microservice.webfluxplayground.section02;

import com.reactive.microservice.webfluxplayground.section02.model.OrderDetails;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;

@Slf4j
public class DatabaseClientTest extends AbstractTest {

    @Autowired
    private DatabaseClient databaseClient;

    @Test
    public void orderDetailsByProduct() {
        String query = """
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
                """;

        this.databaseClient.sql(query)
                   .bind("productDescription", "iphone 20")
                   .mapProperties(OrderDetails.class)
                   .all()
                   .doOnNext(model -> log.info("order details received: {}", model))
                   .as(StepVerifier::create)
                   .assertNext(dto -> Assertions.assertEquals(975, dto.amount()))
                   .assertNext(dto -> Assertions.assertEquals(950, dto.amount()))
                   .expectComplete()
                   .verify();
    }

}
