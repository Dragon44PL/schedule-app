package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkDaysChangeCommand;
import com.github.schedule.workmonth.dto.WorkMonthCreateCommand;
import com.github.schedule.workmonth.event.WorkDaysChangedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.exception.WorkDayOutOfRangeException;
import com.github.schedule.workmonth.exception.WorkMonthExistsException;
import com.github.schedule.workmonth.vo.UserId;

import java.time.YearMonth;
import java.util.*;

public class WorkMonthFacade {

    private final WorkMonthRepository workMonthRepository;

    public WorkMonthFacade(WorkMonthRepository workMonthRepository) {
        this.workMonthRepository = workMonthRepository;
    }

    public List<WorkMonthEvent> createWorkMonth(WorkMonthCreateCommand workMonthCreateCommand) throws WorkMonthExistsException {
        validateWorkMonthAlreadyPresent(workMonthCreateCommand.userId(), workMonthCreateCommand.yearMonth());
        final WorkMonth workMonth = WorkMonth.create(UUID.randomUUID(), workMonthCreateCommand.userId(), workMonthCreateCommand.yearMonth());
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

    private void validateWorkMonthAlreadyPresent(UserId userId, YearMonth yearMonth) {
        final Optional<WorkMonth> requestedWorkMonth = workMonthRepository.findByUser(userId, yearMonth);
        requestedWorkMonth.ifPresent(found -> {
            throw new WorkMonthExistsException(
                String.format("There is already present WorkMonth for userId = '%s', year = '%s' and month = '%s'", userId, yearMonth.getYear(), yearMonth.getMonthValue())
            );
        });
    }
}