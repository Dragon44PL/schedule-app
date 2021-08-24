package com.github.schedule.workmonth.event;

import com.github.schedule.workmonth.vo.WorkDay;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record WorkMonthCreatedEvent(Instant occurredOn, UUID aggregateId, LocalDate startingDate, LocalDate endingDate, Set<WorkDay> workDays) implements WorkMonthEvent {

    public WorkMonthCreatedEvent(UUID workMonthId, LocalDate startingDate, LocalDate endingDate, Set<WorkDay> workDays) {
        this(Instant.now(), workMonthId, startingDate, endingDate, workDays);
    }
}