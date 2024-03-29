package com.github.schedule.workmonth;

import com.github.schedule.workmonth.vo.UserId;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;

import java.time.YearMonth;
import java.util.Set;
import java.util.stream.Collectors;

class WorkMonthEntityConverter {

    static WorkMonth convertWorkMonth(WorkMonthEntity workMonthEntity) {
        final Set<WorkDay> workDays = workMonthEntity.getWorkDays().stream().map(WorkMonthEntityConverter::convertWorkDay).collect(Collectors.toSet());
        final YearMonthEntity yearMonth = workMonthEntity.getYearMonth();
        return WorkMonth.restore(workMonthEntity.getId(), new UserId(workMonthEntity.getUserId()), YearMonth.of(yearMonth.getYear(), yearMonth.getMonth()), workDays);
    }

    static WorkDay convertWorkDay(WorkDayEntity workDayEntity) {
        final WorkHour startingHour = new WorkHour(workDayEntity.getStartingHour().getHour(), workDayEntity.getStartingHour().getMinute());
        final WorkHour endingHour = new WorkHour(workDayEntity.getEndingHour().getHour(), workDayEntity.getEndingHour().getMinute());
        return new WorkDay(workDayEntity.getDate(), startingHour, endingHour, workDayEntity.isLeave());
    }
}
