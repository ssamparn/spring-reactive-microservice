package com.reactive.microservice.aggregatorservice.model.response;

import java.util.List;

/* *
 * Represents the customer portfolio response model provided to the browser.
 * Formed from CustomerInformationResponse received from Customer Service
 * */
public record CustomerPortfolioResponse(Integer customerId,
                                        String customerName,
                                        Integer balance,
                                        List<Holding> holdings) {

}
