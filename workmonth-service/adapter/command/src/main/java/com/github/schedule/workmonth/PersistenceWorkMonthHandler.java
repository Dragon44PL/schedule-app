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
class PersistenceWorkMonthHandler implements DomainEventHandler<WorkMonthEvent> {

    private final WorkMonthCreatedHandler workMonthCreatedHandler;
    private final WorkDaysChangedHandler workDaysChangedHandler;
    private final TotalHoursCalculatedHandler totalHoursCalculatedHandler;

    @Override
    public void handle(WorkMonthEvent workMonthEvent) {

        if(workMonthEvent instanceof WorkMonthCreatedEvent) {
            workMonthCreatedHandler.handle((WorkMonthCreatedEvent) workMonthEvent);
        } else if(workMonthEvent instanceof WorkDaysChangedEvent) {
            workDaysChangedHandler.handle((WorkDaysChangedEvent) workMonthEvent);
        } else if(workMonthEvent instanceof TotalHoursCalculatedEvent) {
            totalHoursCalculatedHandler.handle((TotalHoursCalculatedEvent) workMonthEvent);
        }
    }

}
