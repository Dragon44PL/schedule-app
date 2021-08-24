package com.github.schedule.workmonth;

import com.github.schedule.core.AggregateRoot;
import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.event.WorkDayUpdatedEvent;
import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.exception.WorkDayInvalidException;
import com.github.schedule.workmonth.vo.WorkDate;
import com.github.schedule.workmonth.vo.WorkHour;
import com.github.schedule.workmonth.vo.WorkDay;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

class WorkMonth extends AggregateRoot<UUID, WorkMonthEvent> {

    private final UUID id;
    private final LocalDate startingDate;
    private final LocalDate endingDate;
    private final Set<WorkDay> workDays;

    static WorkMonth restore(UUID id, WorkDate workDate, Set<WorkDay> workDays) throws WorkDayInvalidException {
        final LocalDate startingDate = LocalDate.of(workDate.year(), workDate.month(), 1);
        return new WorkMonth(id, startingDate, workDays, new ArrayList<>());
    }

    static WorkMonth create(UUID id, WorkDate workDate) {
        final LocalDate startingDate = LocalDate.of(workDate.year(), workDate.month(), 1);
        final WorkMonth workMonth = new WorkMonth(id, startingDate, generateWorkDays(startingDate, startingDate.withDayOfMonth(startingDate.lengthOfMonth())), new ArrayList<>());
        final WorkDate workMonthDate = new WorkDate(workMonth.startingDate.getYear(), workMonth.startingDate.getMonth().getValue());
        workMonth.registerEvent(new WorkMonthCreatedEvent(workMonth.id, workMonthDate, workMonth.workDays));
        return workMonth;
    }

    private WorkMonth(UUID id, LocalDate startingDate, Set<WorkDay> workDays, List<WorkMonthEvent> events) {
        super(events);
        this.id = id;
        this.startingDate = startingDate;
        this.endingDate = startingDate.withDayOfMonth(startingDate.lengthOfMonth());
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

    void changeWorkDay(WorkDay another) throws WorkDayInvalidException {
        checkWorkDay(another);
        processChangingWorkDay(another);
    }

    private void processChangingWorkDay(WorkDay another) {
        final Optional<WorkDay> workDay = workDays.stream().filter(day -> day.same(another.date())).findAny();
        workDay.ifPresent((found) -> {
            replaceWorkDay(found);
            this.registerEvent(new WorkDayUpdatedEvent(this.id, found));
        });
    }

    private void replaceWorkDay(WorkDay workDay) {
        workDays.removeIf((found) -> workDay.same(found.date()));
        workDays.add(workDay);
    }

    private void checkWorkDay(WorkDay workDay) {
        if(!(workDay.notLesserThan(startingDate) && workDay.notGreaterThan(endingDate))) {
            throw new WorkDayInvalidException();
        }
    }

}
