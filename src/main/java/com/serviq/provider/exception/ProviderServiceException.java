package com.serviq.provider.exception;

public class ProviderServiceException extends RuntimeException {
    public ProviderServiceException(String message) {
        super(message);
    }

    public ProviderServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
