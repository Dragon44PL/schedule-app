package com.github.schedule.core;

import java.util.Optional;

public interface DomainRepository<I, T extends Aggregate> {
    Optional<T> findById(I i);
    void save(T t );
}