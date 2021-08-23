package com.github.schedule.workmonth.event;

import com.github.schedule.workmonth.vo.WorkDay;

import java.time.Instant;
import java.util.UUID;

public record WorkDayUpdatedEvent(Instant occurredOn, UUID aggregateId, WorkDay workDay) implements WorkMonthEvent {

    public WorkDayUpdatedEvent(UUID workMonthId, WorkDay workDay) {
        this(Instant.now(), workMonthId, workDay);
    }
}