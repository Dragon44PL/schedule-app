package com.github.schedule.workmonth;

import com.github.schedule.core.events.DomainEventHandler;
import com.github.schedule.workmonth.event.WorkDaysChangedEvent;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Component
class WorkDaysChangedHandler implements DomainEventHandler<WorkDaysChangedEvent> {

    private final WorkMonthEntityRepository workMonthEntityRepository;

    WorkDaysChangedHandler(WorkMonthEntityRepository workMonthEntityRepository) {
        this.workMonthEntityRepository = workMonthEntityRepository;
    }

    @Override
    public void handle(WorkDaysChangedEvent workDaysChangedEvent) {
        final Optional<WorkMonthEntity> workMonthEntity = workMonthEntityRepository.findById(workDaysChangedEvent.aggregateId());
        workMonthEntity.ifPresent((found) -> updateWorkDays(workDaysChangedEvent, found));
    }

    private void updateWorkDays(WorkDaysChangedEvent workDaysChangedEvent, WorkMonthEntity workMonthEntity) {
        final Set<WorkDay> workDays = workDaysChangedEvent.workDays();
        workDays.forEach(workDay -> updateWorkDay(workDay, workMonthEntity));
        workMonthEntityRepository.save(workMonthEntity);
    }

    private void updateWorkDay(WorkDay workDay, WorkMonthEntity workMonthEntity) {
        final Optional<WorkDayEntity> workDayEntity = workMonthEntity.getWorkDays().stream()
                .filter((entity) -> entity.getDate().isEqual(workDay.date()))
                .findFirst();
        workDayEntity.ifPresent((found) -> updateWorkDayEntity(found, workDay));
    }

    private void updateWorkDayEntity(WorkDayEntity source, WorkDay target) {
        final LocalDate date = target.date();
        source.setStartingHour(localDateTime(date, target.startingHour()));
        source.setEndingHour(localDateTime(date, target.endingHour()));
        source.setLeave(target.isLeave());
    }

    private LocalDateTime localDateTime(LocalDate localDate, WorkHour workHour) {
        return LocalDateTime.of(localDate.getYear(), localDate.getMonth().getValue(), localDate.getDayOfMonth(), workHour.hours(), workHour.minutes());
    }
}
