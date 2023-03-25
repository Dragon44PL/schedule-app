package com.github.schedule.workmonth;

import com.github.schedule.core.events.DomainEventPublisher;
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
    private final DomainEventPublisher<WorkMonthEvent> workMonthEventPublisher;

    public WorkMonthFacade(WorkMonthRepository workMonthRepository, DomainEventPublisher<WorkMonthEvent> workMonthEventPublisher) {
        this.workMonthRepository = workMonthRepository;
        this.workMonthEventPublisher = workMonthEventPublisher;
    }

    public List<WorkMonthEvent> createWorkMonth(WorkMonthCreateCommand workMonthCreateCommand) throws WorkMonthExistsException {
        validateWorkMonthAlreadyPresent(workMonthCreateCommand.userId(), workMonthCreateCommand.yearMonth());
        final WorkMonth workMonth = WorkMonth.create(UUID.randomUUID(), workMonthCreateCommand.userId(), workMonthCreateCommand.yearMonth());
        workMonthRepository.save(workMonth);
        final List<WorkMonthEvent> workMonthEvents = workMonth.findLatestEvent().stream().toList();
        publishEvents(workMonthEvents);
        return workMonthEvents;
    }

    public List<WorkMonthEvent> updateWorkDays(WorkDaysChangeCommand workDaysChangeCommand) throws WorkDayOutOfRangeException {
        Optional<WorkMonth> workMonth = workMonthRepository.findById(workDaysChangeCommand.id());
        final List<WorkMonthEvent> workMonthEvents = workMonth.map(month -> processUpdatingWorkDays(month, workDaysChangeCommand)).orElseGet(ArrayList::new);
        publishEvents(workMonthEvents);
        return workMonthEvents;
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

    private void publishEvents(List<WorkMonthEvent> workMonthEvents) {
        workMonthEvents.forEach(workMonthEventPublisher::publish);
    }

    private void validateWorkMonthAlreadyPresent(UserId userId, YearMonth yearMonth) {
        final Optional<WorkMonth> requestedWorkMonth = workMonthRepository.findByUser(userId, yearMonth);
        requestedWorkMonth.ifPresent(found -> {
            throw new WorkMonthExistsException(
                String.format(
                    "There is already present WorkMonth for userId = '%s', year = '%s' and month = '%s'",
                    userId,
                    yearMonth.getYear(),
                    yearMonth.getMonthValue()
                )
            );
        });
    }
}