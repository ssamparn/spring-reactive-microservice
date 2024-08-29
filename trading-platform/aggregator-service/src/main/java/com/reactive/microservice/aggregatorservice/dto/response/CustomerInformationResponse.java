package com.reactive.microservice.aggregatorservice.dto.response;

import com.reactive.microservice.aggregatorservice.model.response.Holding;

import java.util.List;

/* *
 * Represents the response body for GET call to Customer Portfolio Service to fetch customer portfolio.
 * */
public record CustomerInformationResponse(
        Integer customerId,
        String customerName,
        Integer balance,
        List<Holding> holdings) {

}
