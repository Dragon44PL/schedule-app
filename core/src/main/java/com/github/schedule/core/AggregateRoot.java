package com.github.schedule.core;

import com.github.schedule.core.events.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AggregateRoot<I, E extends DomainEvent<I>> implements Aggregate {

    private final List<E> events;

    public AggregateRoot(List<E> events) {
        this.events = events;
    }

    protected final void registerEvent(E event) {
        this.events.add(event);
    }

    public final List<E> events() {
        return new ArrayList<>(events);
    }

    public final Optional<E> findLatestEvent() {
        final int count = events.size();
        return (count != 0) ? events.stream().skip(count - 1).findFirst() : Optional.empty();
    }
}
