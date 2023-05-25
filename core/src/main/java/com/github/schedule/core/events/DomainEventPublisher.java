package com.github.schedule.core.events;

public interface DomainEventPublisher<T extends DomainEvent<?>> {
    void publish(T t);
}
