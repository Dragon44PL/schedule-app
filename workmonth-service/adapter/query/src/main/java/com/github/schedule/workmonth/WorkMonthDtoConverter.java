package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkDayQueryDto;
import com.github.schedule.workmonth.dto.WorkHourQueryDto;
import com.github.schedule.workmonth.dto.WorkMonthQueryDto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

class WorkMonthDtoConverter {

    static WorkMonthQueryDto workMonthDto(WorkMonthEntity workMonthEntity) {
        final Set<WorkDayQueryDto> workDays = workMonthEntity.getWorkDays().stream().map(WorkMonthDtoConverter::workDayDto).collect(Collectors.toSet());
        return new WorkMonthQueryDto(workMonthEntity.getId(), workMonthEntity.getUserId(), workMonthEntity.getDate(), workHourDto(workMonthEntity.getTotalHours()), workDays);
    }

    static WorkDayQueryDto workDayDto(WorkDayEntity workDayEntity) {
        return new WorkDayQueryDto(workDayEntity.getDate(), workHourDto(workDayEntity.getStartingHour()), workHourDto(workDayEntity.getEndingHour()), workDayEntity.isLeave());
    }

    static WorkHourQueryDto workHourDto(LocalDateTime localDateTime) {
        return new WorkHourQueryDto(localDateTime.getHour(), localDateTime.getMinute());
    }

    static WorkHourQueryDto workHourDto(WorkHourEntity workHourEntity) {
        return new WorkHourQueryDto(workHourEntity.getHours(), workHourEntity.getMinutes());
    }

}
