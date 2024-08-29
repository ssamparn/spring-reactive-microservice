package com.reactive.microservice.aggregatorservice.model.response;

import java.util.List;

public record CustomerInformationResponse(Integer customerId,
                                          String customerName,
                                          Integer balance,
                                          List<Holding> holdings) {

}
