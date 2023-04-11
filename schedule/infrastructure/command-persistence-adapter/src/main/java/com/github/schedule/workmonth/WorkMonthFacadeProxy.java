package com.github.schedule.workmonth;

import com.github.schedule.core.events.DomainEventPublisher;
import com.github.schedule.workmonth.dto.WorkDaysChangeCommand;
import com.github.schedule.workmonth.dto.WorkMonthCreateCommand;
import com.github.schedule.workmonth.event.WorkMonthEvent;

import com.github.schedule.workmonth.exception.AccountNotFoundException;
import com.github.schedule.workmonth.integration.AccountServiceIntegration;
import com.github.schedule.workmonth.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkMonthFacadeProxy extends WorkMonthFacade {

    private final AccountServiceIntegration accountServiceIntegration;

    public WorkMonthFacadeProxy(WorkMonthRepository workMonthRepository, DomainEventPublisher<WorkMonthEvent> workMonthEventPublisher, AccountServiceIntegration accountServiceIntegration) {
        super(workMonthRepository, workMonthEventPublisher);
        this.accountServiceIntegration = accountServiceIntegration;
    }

    @Override
    @Transactional
    public List<WorkMonthEvent> createWorkMonth(WorkMonthCreateCommand workMonthCreateCommand) {
        validateAccountAlreadyExists(workMonthCreateCommand.userId());
        return super.createWorkMonth(workMonthCreateCommand);
    }

    private void validateAccountAlreadyExists(UserId userId) {
        if(!accountServiceIntegration.accountExists(userId.id())) {
            throw new AccountNotFoundException(
                String.format("Account with id = '%s' could not been found", userId)
            );
        }
    }

    @Override
    @Transactional
    public List<WorkMonthEvent> updateWorkDays(WorkDaysChangeCommand workDaysChangeCommand) {
        return super.updateWorkDays(workDaysChangeCommand);
    }
}
