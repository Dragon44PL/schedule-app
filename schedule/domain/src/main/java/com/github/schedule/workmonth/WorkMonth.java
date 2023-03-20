package com.github.schedule.workmonth;

import com.github.schedule.core.AggregateRoot;
import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.event.WorkDaysChangedEvent;
import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.exception.WorkDayOutOfRangeException;
import com.github.schedule.workmonth.vo.UserId;
import com.github.schedule.workmonth.vo.WorkHour;
import com.github.schedule.workmonth.vo.WorkDay;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

class WorkMonth extends AggregateRoot<UUID, WorkMonthEvent> {

    private final UUID id;
    private final UserId userId;
    private final YearMonth yearMonth;
    private final Set<WorkDay> workDays;

    static WorkMonth restore(UUID id, UserId userId, YearMonth yearMonth, Set<WorkDay> workDays, List<WorkMonthEvent> events) throws WorkDayOutOfRangeException {
        return new WorkMonth(id, userId, yearMonth, workDays, events);
    }

    static WorkMonth restore(UUID id, UserId userId, YearMonth yearMonth, Set<WorkDay> workDays) throws WorkDayOutOfRangeException {
        return new WorkMonth(id, userId, yearMonth, workDays, new ArrayList<>());
    }

    static WorkMonth create(UUID id, UserId userId, YearMonth yearMonth) {
        final WorkMonth workMonth = new WorkMonth(id, userId, yearMonth, generateWorkDays(yearMonth.atDay(1), yearMonth.atEndOfMonth()), new ArrayList<>());
        workMonth.registerEvent(new WorkMonthCreatedEvent(workMonth.id, workMonth.userId, workMonth.yearMonth, WorkHour.zero(), workMonth.workDays));
        return workMonth;
    }

    private WorkMonth(UUID id, UserId userId, YearMonth yearMonth, Set<WorkDay> workDays, List<WorkMonthEvent> events) {
        super(events);
        this.id = id;
        this.userId = userId;
        this.yearMonth = yearMonth;
        this.workDays = workDays;
    }

    private static Set<WorkDay> generateWorkDays(final LocalDate startingDate, final LocalDate endingDate) {
        return startingDate.datesUntil(endingDate.plusDays(1))
                .map((date) -> new WorkDay(date, false))
                .collect(Collectors.toSet());
    }

    void calculateTotalHours() {

        final WorkHour totalWorkHours = workDays.stream()
                .filter(workDay -> !workDay.isLeave())
                .map(WorkDay::calculateTotalTime)
                .reduce(WorkHour.zero(), WorkHour::add);

        final TotalHoursCalculatedEvent totalHoursCalculatedEvent = new TotalHoursCalculatedEvent(this.id, totalWorkHours);
        this.registerEvent(totalHoursCalculatedEvent);
    }

    void changeWorkDays(Set<WorkDay> workDays) throws WorkDayOutOfRangeException {
        workDays.forEach(this::checkWorkDay);
        final Set<WorkDay> changed = workDays.stream()
                                        .map(this::processChangingWorkDay).filter(Optional::isPresent)
                                        .map(Optional::get).collect(Collectors.toSet());

        if(changed.size() > 0) {
            this.registerEvent(new WorkDaysChangedEvent(this.id, changed));
        }
    }

    private Optional<WorkDay> processChangingWorkDay(WorkDay another) {
        final Optional<WorkDay> workDay = workDays.stream().filter(day -> day.sameDate(another.date())).findAny();
        return workDay.isPresent() && workDay.get().contentChanged(another) ? Optional.of(replaceWorkDay(another)) : Optional.empty();
    }

    private WorkDay replaceWorkDay(WorkDay another) {
        workDays.removeIf((found) -> another.sameDate(found.date()));
        workDays.add(another);
        return another;
    }

    private void checkWorkDay(WorkDay workDay) {
        if(!(workDay.notLesserThan(yearMonth.atDay(1)) && workDay.notGreaterThan(yearMonth.atEndOfMonth()))) {
            final String message = String.format("WorkDay Out of Range: '%s' - From '%s' to '%s'", workDay, yearMonth.atDay(1), yearMonth.atEndOfMonth());
            throw new WorkDayOutOfRangeException(message);
        }
    }
}