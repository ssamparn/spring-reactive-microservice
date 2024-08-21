package com.reactive.microservice.customerportfolio.model.response;

import com.reactive.microservice.customerportfolio.model.Holding;

import java.util.List;

public record CustomerInformationResponse(
        Integer customerId,
        String customerName,
        Integer balance,
        List<Holding> holdings) {

}
