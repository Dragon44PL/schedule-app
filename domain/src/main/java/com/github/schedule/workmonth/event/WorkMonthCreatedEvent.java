package com.github.schedule.workmonth.event;

import com.github.schedule.workmonth.vo.WorkDate;
import com.github.schedule.workmonth.vo.WorkDay;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record WorkMonthCreatedEvent(Instant occurredOn, UUID aggregateId, WorkDate workDate, Set<WorkDay> workDays) implements WorkMonthEvent {

    public WorkMonthCreatedEvent(UUID workMonthId, WorkDate workDate, Set<WorkDay> workDays) {
        this(Instant.now(), workMonthId, workDate, workDays);
    }
}