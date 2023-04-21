package com.github.schedule.workmonth.converter;

import com.github.schedule.workmonth.command.WorkDaysChangeCommand;
import com.github.schedule.workmonth.command.WorkMonthCreateCommand;
import com.github.schedule.workmonth.dto.request.workday.WorkDayRequestDto;
import com.github.schedule.workmonth.dto.request.workday.WorkDaysChangeDto;
import com.github.schedule.workmonth.dto.request.workday.WorkHourRequestDto;
import com.github.schedule.workmonth.dto.request.workmonth.WorkMonthCreateDto;
import com.github.schedule.workmonth.vo.UserId;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorkMonthRequestConverter {

    public static WorkMonthCreateCommand convertWorkMonthCreateCommand(WorkMonthCreateDto workMonthCreateDto) {
        final YearMonth yearMonth = YearMonth.of(workMonthCreateDto.getYear(), workMonthCreateDto.getMonth());
        final UserId userId = new UserId(UUID.fromString(workMonthCreateDto.getUserId()));
        return new WorkMonthCreateCommand(yearMonth, userId);
    }

    public static WorkDaysChangeCommand convertWorkDaysChangedCommand(String id, WorkDaysChangeDto workDaysChangeDto) {
        final Set<WorkDay> workDays = workDaysChangeDto.getWorkDays().stream().map(WorkMonthRequestConverter::convertWorkDay).collect(Collectors.toSet());
        return new WorkDaysChangeCommand(UUID.fromString(id), workDays);
    }

    static WorkDay convertWorkDay(WorkDayRequestDto workDayRequestDto) {
        final WorkHour startingHour = WorkMonthRequestConverter.convertWorkHour(workDayRequestDto.getStartingHour());
        final WorkHour endingHour = WorkMonthRequestConverter.convertWorkHour(workDayRequestDto.getEndingHour());
        return new WorkDay(workDayRequestDto.getDate(), startingHour, endingHour, workDayRequestDto.getLeave());
    }

    static WorkHour convertWorkHour(WorkHourRequestDto workHourRequestDto) {
        return new WorkHour(workHourRequestDto.getHour(), workHourRequestDto.getMinute());
    }
}
