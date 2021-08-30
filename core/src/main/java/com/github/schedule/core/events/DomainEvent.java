package com.github.schedule.core.events;

import java.time.Instant;

public interface DomainEvent<I> {
    I aggregateId();
    Instant occurredOn();
}
