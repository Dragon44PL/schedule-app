package com.github.schedule.core.events;

public interface DomainEventHandler<T extends DomainEvent> {
    void handle(T t);
}
