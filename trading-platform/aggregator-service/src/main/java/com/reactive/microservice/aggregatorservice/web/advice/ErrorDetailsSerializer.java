package com.reactive.microservice.aggregatorservice.web.advice;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ErrorDetailsSerializer extends JsonSerializer<ErrorDetails> {
    @Override
    public void serialize(ErrorDetails errorDetails, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("code", errorDetails.getErrorCode());
        jsonGenerator.writeStringField("message", errorDetails.getErrorMessage());
        jsonGenerator.writeStringField("reference", errorDetails.getReferenceUrl());
        jsonGenerator.writeEndObject();
    }
}
