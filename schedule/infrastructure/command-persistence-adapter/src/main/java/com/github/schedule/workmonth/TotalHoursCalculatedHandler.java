package com.github.schedule.workmonth;

import com.github.schedule.core.events.DomainEventHandler;
import com.github.schedule.workmonth.WorkHourEntity;
import com.github.schedule.workmonth.WorkMonthEntity;
import com.github.schedule.workmonth.WorkMonthEntityRepository;
import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.vo.WorkHour;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class TotalHoursCalculatedHandler implements DomainEventHandler<TotalHoursCalculatedEvent> {

    private final WorkMonthEntityRepository workMonthEntityRepository;

    @Override
    public void handle(TotalHoursCalculatedEvent totalHoursCalculatedEvent) {
        final Optional<WorkMonthEntity> workMonthEntity = workMonthEntityRepository.findById(totalHoursCalculatedEvent.aggregateId());
        workMonthEntity.ifPresent((found) -> processTotalHoursCalculated(found, totalHoursCalculatedEvent));
    }

    private void processTotalHoursCalculated(WorkMonthEntity workMonthEntity, TotalHoursCalculatedEvent totalHoursCalculatedEvent) {
        final WorkHour workHour = totalHoursCalculatedEvent.totalWorkHours();
        final WorkHourEntity workHourEntity = WorkHourEntity.builder().hours(workHour.hours()).minutes(workHour.minutes()).build();
        workMonthEntity.setTotalHours(workHourEntity);
        workMonthEntityRepository.save(workMonthEntity);
    }
}
