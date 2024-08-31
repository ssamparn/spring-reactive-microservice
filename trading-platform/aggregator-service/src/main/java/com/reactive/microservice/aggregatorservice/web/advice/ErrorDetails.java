package com.reactive.microservice.aggregatorservice.web.advice;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@JsonSerialize(using = ErrorDetailsSerializer.class)
public enum ErrorDetails {

    CUSTOMER_NOT_FOUND("404-Stock-Aggregator", "Customer not found", "http://example.com/123"),
    INVALID_TRADE_REQUEST("400-Stock-Aggregator", "Invalid Trade Request", "http://example.com/123");

    @Getter
    private String errorCode;
    @Getter
    private String errorMessage;
    @Getter
    private String referenceUrl;

    ErrorDetails(String errorCode, String errorMessage, String referenceUrl) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.referenceUrl = referenceUrl;
    }
}
