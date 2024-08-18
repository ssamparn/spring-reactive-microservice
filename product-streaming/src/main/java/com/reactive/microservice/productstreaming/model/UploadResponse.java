package com.reactive.microservice.productstreaming.model;

import java.util.UUID;

public record UploadResponse(UUID confirmationId,
                             Long productsCount) {
}
