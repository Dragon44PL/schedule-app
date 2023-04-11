package com.github.schedule.workmonth;

import com.github.schedule.core.events.DomainEventHandler;
import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class WorkMonthCreatedHandler implements DomainEventHandler<WorkMonthCreatedEvent> {

    private final WorkMonthEntityRepository workMonthEntityRepository;

    @Override
    public void handle(WorkMonthCreatedEvent workMonthCreatedEvent) {
        final Set<WorkDayEntity> workDays = workMonthCreatedEvent.workDays().stream()
                    .map(WorkMonthCreatedHandler::workDay)
                    .collect(Collectors.toSet());

        final WorkHour workHour = workMonthCreatedEvent.totalHours();
        final YearMonth yearMonth = workMonthCreatedEvent.yearMonth();
        final WorkMonthEntity workMonthEntity = WorkMonthEntity.builder()
                .id(workMonthCreatedEvent.aggregateId())
                .userId(workMonthCreatedEvent.userId().id())
                .yearMonth(new YearMonthEntity(yearMonth.getYear(), yearMonth.getMonth().getValue()))
                .totalHours(WorkHourEntity.builder().hours(workHour.hours()).minutes(workHour.minutes()).build())
                .workDays(workDays)
                .build();

        workMonthEntity.getWorkDays().forEach(entity -> entity.setWorkMonth(workMonthEntity));
        workMonthEntityRepository.saveAndFlush(workMonthEntity);
    }

    private static WorkDayEntity workDay(WorkDay workDay) {
        final LocalDate date = workDay.date();
        return WorkDayEntity.builder()
                .date(date)
                .startingHour(localDateTime(date, workDay.startingHour()))
                .endingHour(localDateTime(date, workDay.endingHour()))
                .isLeave(workDay.isLeave())
                .build();
    }

    private static LocalDateTime localDateTime(LocalDate date, WorkHour workHour) {
        return LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), workHour.hours(), workHour.minutes());
    }

}
