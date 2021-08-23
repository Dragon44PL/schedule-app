package com.github.schedule.workmonth.event;

import com.github.schedule.workmonth.vo.WorkHour;

import java.time.Instant;
import java.util.UUID;

public record TotalHoursCalculatedEvent(Instant occurredOn, UUID aggregateId, WorkHour totalWorkHour) implements WorkMonthEvent {

    public TotalHoursCalculatedEvent(UUID workMonthId, WorkHour totalWorkHour) {
        this(Instant.now(), workMonthId, totalWorkHour);
    }
}