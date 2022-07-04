package com.github.schedule.workmonth;

import com.github.schedule.workmonth.command.WorkDaysChangeCommand;
import com.github.schedule.workmonth.command.WorkMonthCreateCommand;
import com.github.schedule.workmonth.event.WorkMonthEvent;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkMonthFacadeProxy extends WorkMonthFacade {

    public WorkMonthFacadeProxy(WorkMonthRepository workMonthRepository) {
        super(workMonthRepository);
    }

    @Override
    @Transactional
    public List<WorkMonthEvent> createWorkMonth(WorkMonthCreateCommand workMonthCreateCommand) {
        return super.createWorkMonth(workMonthCreateCommand);
    }

    @Override
    @Transactional
    public List<WorkMonthEvent> updateWorkDays(WorkDaysChangeCommand workDaysChangeCommand) {
        return super.updateWorkDays(workDaysChangeCommand);
    }
}
