package com.reactive.microservice.customerportfolio.entity;

import com.reactive.microservice.customerportfolio.domain.Ticker;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
public class PortfolioItem {

    @Id
    private Integer id;
    private Integer customerId;
    private Ticker ticker;
    private Integer quantity;
}
