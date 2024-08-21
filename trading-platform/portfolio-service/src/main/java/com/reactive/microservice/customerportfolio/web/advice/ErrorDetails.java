package com.reactive.microservice.customerportfolio.web.advice;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@JsonSerialize(using = ErrorDetailsSerializer.class)
public enum ErrorDetails {
    CUSTOMER_NOT_FOUND(123, "Customer not found", "http://example.com/123");

    @Getter
    private Integer errorCode;
    @Getter
    private String errorMessage;
    @Getter
    private String referenceUrl;

    ErrorDetails(Integer errorCode, String errorMessage, String referenceUrl) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.referenceUrl = referenceUrl;
    }

}
