package com.serviq.provider.exception;

public class ProviderServiceConflictException extends RuntimeException {
    public ProviderServiceConflictException(String message) {
        super(message);
    }

    public ProviderServiceConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
