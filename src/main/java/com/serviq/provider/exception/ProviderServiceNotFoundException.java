package com.serviq.provider.exception;

import java.util.UUID;

public class ProviderServiceNotFoundException extends RuntimeException {
    public ProviderServiceNotFoundException(UUID id) {
        super(String.format("Provider service not found with id: %s", id));
    }

    public ProviderServiceNotFoundException(String message) {
        super(message);
    }
}
