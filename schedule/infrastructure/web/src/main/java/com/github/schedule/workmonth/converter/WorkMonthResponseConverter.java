package com.github.schedule.workmonth.converter;

import com.github.schedule.workmonth.dto.*;
import com.github.schedule.workmonth.dto.response.WorkDayHourResponseDto;
import com.github.schedule.workmonth.dto.response.WorkDayResponseDto;
import com.github.schedule.workmonth.dto.response.WorkMonthHourResponseDto;
import com.github.schedule.workmonth.dto.response.WorkMonthResponseDto;

import java.util.stream.Collectors;

public class WorkMonthResponseConverter {

    public static WorkMonthResponseDto convertWorkMonthResponseDto(WorkMonthQueryDto workMonthQueryDto) {
        return WorkMonthResponseDto.builder().id(workMonthQueryDto.id())
                .userId(workMonthQueryDto.userId()).yearMonth(workMonthQueryDto.date())
                .totalHours(convertWorkMonthHourResponseDto(workMonthQueryDto.totalHours()))
                .workDays(workMonthQueryDto.workDays().stream().map(WorkMonthResponseConverter::convertWorkDayResponseDto).collect(Collectors.toSet()))
                .build();
    }

    static WorkDayResponseDto convertWorkDayResponseDto(WorkDayQueryDto workDayQueryDto) {
        return WorkDayResponseDto.builder().date(workDayQueryDto.date())
                .startingHour(convertWorkDayHourResponseDto(workDayQueryDto.startingHour()))
                .endingHour(convertWorkDayHourResponseDto(workDayQueryDto.endingHour()))
                .isLeave(workDayQueryDto.isLeave()).build();
    }

    static WorkMonthHourResponseDto convertWorkMonthHourResponseDto(WorkHourQueryDto workHourQueryDto) {
        return WorkMonthHourResponseDto.builder().hours(workHourQueryDto.hour()).minutes(workHourQueryDto.minutes()).build();
    }

    static WorkDayHourResponseDto convertWorkDayHourResponseDto(WorkHourQueryDto workHourQueryDto) {
        return WorkDayHourResponseDto.builder().hour(workHourQueryDto.hour()).minute(workHourQueryDto.minutes()).build();
    }
}