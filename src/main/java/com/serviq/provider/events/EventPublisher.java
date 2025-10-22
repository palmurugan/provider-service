package com.serviq.provider.events;

public interface EventPublisher<T> {
    void publish(T event);
}
