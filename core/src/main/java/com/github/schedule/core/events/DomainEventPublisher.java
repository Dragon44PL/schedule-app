package com.github.schedule.core.events;

public interface DomainEventPublisher {
    void publish(DomainEvent t);
}
