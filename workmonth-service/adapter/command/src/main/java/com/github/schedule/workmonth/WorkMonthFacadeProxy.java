package com.github.schedule.workmonth;

import com.github.schedule.workmonth.command.WorkDaysChangeCommand;
import com.github.schedule.workmonth.command.WorkMonthCreateCommand;
import com.github.schedule.workmonth.event.WorkMonthEvent;

import com.github.schedule.workmonth.exception.AccountNotFoundException;
import com.github.schedule.workmonth.integration.AccountServiceIntegration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkMonthFacadeProxy extends WorkMonthFacade {

    private final AccountServiceIntegration accountServiceIntegration;
    
    public WorkMonthFacadeProxy(WorkMonthRepository workMonthRepository, AccountServiceIntegration accountServiceIntegration) {
        super(workMonthRepository);
        this.accountServiceIntegration = accountServiceIntegration;
    }

    @Override
    @Transactional
    public List<WorkMonthEvent> createWorkMonth(WorkMonthCreateCommand workMonthCreateCommand) {
        final UUID userId = workMonthCreateCommand.userId().id();

        if(!accountServiceIntegration.accountExists(userId)) {
            throw new AccountNotFoundException(
               String.format("Account with id = '%s' could not been found", userId)
            );
        }

        return super.createWorkMonth(workMonthCreateCommand);
    }

    @Override
    @Transactional
    public List<WorkMonthEvent> updateWorkDays(WorkDaysChangeCommand workDaysChangeCommand) {
        return super.updateWorkDays(workDaysChangeCommand);
    }
}
