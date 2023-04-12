package com.github.schedule.workmonth;

import com.github.schedule.core.events.DomainEventHandler;
import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.event.WorkDaysChangedEvent;
import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class WorkMonthEventHandler implements DomainEventHandler<WorkMonthEvent> {

    private final DomainEventHandler<WorkMonthCreatedEvent> workMonthCreatedHandler;
    private final DomainEventHandler<WorkDaysChangedEvent> workDaysChangedHandler;
    private final DomainEventHandler<TotalHoursCalculatedEvent> totalHoursCalculatedHandler;

    @Override
    public void handle(WorkMonthEvent workMonthEvent) {
        switch (workMonthEvent) {
            case WorkMonthCreatedEvent event -> workMonthCreatedHandler.handle(event);
            case WorkDaysChangedEvent event -> workDaysChangedHandler.handle(event);
            case TotalHoursCalculatedEvent event -> totalHoursCalculatedHandler.handle(event);
            default -> {}
        }
    }

}
