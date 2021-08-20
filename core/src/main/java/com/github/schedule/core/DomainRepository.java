package com.github.schedule.core;

public interface DomainRepository<I, T extends Aggregate> {
    void deleteById(I id);
    T save(T t);
}
