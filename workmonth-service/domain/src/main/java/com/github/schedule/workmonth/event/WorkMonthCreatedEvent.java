package com.github.schedule.workmonth.event;

import com.github.schedule.workmonth.vo.UserId;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;

import java.time.Instant;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

public record WorkMonthCreatedEvent(Instant occurredOn, UUID aggregateId, UserId userId, YearMonth yearMonth, WorkHour totalHours, Set<WorkDay> workDays) implements WorkMonthEvent {

    public WorkMonthCreatedEvent(UUID workMonthId, UserId userId, YearMonth yearMonth, WorkHour totalHours, Set<WorkDay> workDays) {
        this(Instant.now(), workMonthId, userId, yearMonth, totalHours, workDays);
    }
}