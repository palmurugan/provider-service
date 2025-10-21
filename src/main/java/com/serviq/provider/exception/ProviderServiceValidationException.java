package com.serviq.provider.exception;

public class ProviderServiceValidationException extends RuntimeException {
    public ProviderServiceValidationException(String message) {
        super(message);
    }

    public ProviderServiceValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
