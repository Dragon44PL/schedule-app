package com.github.schedule.workmonth.event;

import com.github.schedule.workmonth.vo.WorkDay;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record WorkDaysChangedEvent(Instant occurredOn, UUID aggregateId, Set<WorkDay> workDays) implements WorkMonthEvent {

    public WorkDaysChangedEvent(UUID workMonthId, Set<WorkDay> workDay) {
        this(Instant.now(), workMonthId, workDay);
    }
}