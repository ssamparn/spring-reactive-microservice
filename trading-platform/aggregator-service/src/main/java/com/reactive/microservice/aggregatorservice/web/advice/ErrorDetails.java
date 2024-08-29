package com.reactive.microservice.aggregatorservice.web.advice;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@JsonSerialize(using = ErrorDetailsSerializer.class)
public enum ErrorDetails {

    CUSTOMER_NOT_FOUND("404-Customer-PortFolio", "Customer not found", "http://example.com/123"),
    INSUFFICIENT_BALANCE("400-Customer-PortFolio", "Insufficient balance", "http://example.com/123"),
    INSUFFICIENT_SHARES("400-Customer-PortFolio", "Insufficient shares", "http://example.com/123");

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
