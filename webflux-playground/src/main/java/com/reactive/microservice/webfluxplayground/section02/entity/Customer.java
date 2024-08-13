package com.reactive.microservice.webfluxplayground.section02.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/* *
 * We do not have @Entity in R2DBC.
 * @Table / @Column are not really required here, but adding it here for reference!
 * */

@ToString
@Getter
@Setter
@Table("customer")
public class Customer {

    @Id
    private Integer id;
    private String name;
    private String email;
}
