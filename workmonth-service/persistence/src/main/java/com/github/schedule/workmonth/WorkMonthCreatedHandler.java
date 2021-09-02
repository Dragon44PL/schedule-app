package com.github.schedule.workmonth;

import com.github.schedule.core.events.DomainEventHandler;
import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

class WorkMonthCreatedHandler implements DomainEventHandler<WorkMonthCreatedEvent> {

    private final WorkMonthEntityRepository workMonthEntityRepository;

    WorkMonthCreatedHandler(WorkMonthEntityRepository workMonthEntityRepository) {
        this.workMonthEntityRepository = workMonthEntityRepository;
    }

    @Override
    public void handle(WorkMonthCreatedEvent workMonthCreatedEvent) {
        final Set<WorkDayEntity> workDays = workMonthCreatedEvent.workDays().stream()
                                                                 .map(this::workDay).collect(Collectors.toSet());

        final WorkHour workHour = workMonthCreatedEvent.totalHours();
        final WorkMonthEntity workMonthEntity = WorkMonthEntity.builder()
                .id(workMonthCreatedEvent.aggregateId())
                .userId(workMonthCreatedEvent.userId().id())
                .date(workMonthCreatedEvent.yearMonth())
                .totalHours(WorkHourEntity.builder().hours(workHour.hours()).minutes(workHour.minutes()).build())
                .workDays(workDays)
                .build();

        //workMonthEntity.getWorkDays().forEach(entity -> entity.setWorkMonth(workMonthEntity));
        workMonthEntityRepository.saveAndFlush(workMonthEntity);
    }

    private WorkDayEntity workDay(WorkDay workDay) {
        final LocalDate date = workDay.date();
        return WorkDayEntity.builder()
                .date(date)
                .startingHour(localDateTime(date, workDay.startingHour()))
                .endingHour(localDateTime(date, workDay.endingHour()))
                .isLeave(workDay.isLeave())
                .build();
    }

    private LocalDateTime localDateTime(LocalDate date, WorkHour workHour) {
        return LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), workHour.hours(), workHour.minutes());
    }

}
