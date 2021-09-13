package com.github.schedule.workmonth;

import com.github.schedule.workmonth.command.WorkDaysChangeCommand;
import com.github.schedule.workmonth.command.WorkMonthCreateCommand;
import com.github.schedule.workmonth.event.WorkDaysChangedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.exception.WorkDayOutOfRangeException;
import com.github.schedule.workmonth.exception.WorkMonthExistsException;

import java.util.*;

public class WorkMonthFacade {

    private final WorkMonthRepository workMonthRepository;

    public WorkMonthFacade(WorkMonthRepository workMonthRepository) {
        this.workMonthRepository = workMonthRepository;
    }

    public List<WorkMonthEvent> createWorkMonth(WorkMonthCreateCommand workMonthCreateCommand) throws WorkMonthExistsException {
        final WorkMonth workMonth = WorkMonth.create(UUID.randomUUID(), workMonthCreateCommand.userId(), workMonthCreateCommand.yearMonth());
        final Optional<WorkMonth> found = workMonthRepository.findByUser(workMonthCreateCommand.userId(), workMonthCreateCommand.yearMonth());
        found.ifPresent(f -> { throw new WorkMonthExistsException(); });

        workMonthRepository.save(workMonth);
        return workMonth.findLatestEvent().stream().toList();
    }

    public List<WorkMonthEvent> updateWorkDays(WorkDaysChangeCommand workDaysChangeCommand) throws WorkDayOutOfRangeException {
        Optional<WorkMonth> workMonth = workMonthRepository.findById(workDaysChangeCommand.id());
        return workMonth.map(month -> processUpdatingWorkDays(month, workDaysChangeCommand)).orElseGet(ArrayList::new);
    }

    private List<WorkMonthEvent> processUpdatingWorkDays(WorkMonth workMonth, WorkDaysChangeCommand workDaysChangeCommand) {
        workMonth.changeWorkDays(workDaysChangeCommand.workDays());
        return workMonth.findLatestEvent().map(event -> postWorkDaysChanged(workMonth))
                .orElseGet(() -> filledWorkDaysChangedEvent(workDaysChangeCommand));
    }

    private List<WorkMonthEvent> postWorkDaysChanged(WorkMonth workMonth) {
        workMonth.calculateTotalHours();
        workMonthRepository.save(workMonth);
        return new ArrayList<>(workMonth.events());
    }

    private List<WorkMonthEvent> filledWorkDaysChangedEvent(WorkDaysChangeCommand workDaysChangeCommand) {
        final WorkDaysChangedEvent event = new WorkDaysChangedEvent(workDaysChangeCommand.id(), workDaysChangeCommand.workDays());
        return new ArrayList<>(Collections.singleton(event));
    }
}