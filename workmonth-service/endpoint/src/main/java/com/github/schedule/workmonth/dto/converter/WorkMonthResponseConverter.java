package com.github.schedule.workmonth.dto.converter;

import com.github.schedule.workmonth.dto.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class WorkMonthResponseConverter {

    public WorkMonthResponseDto convertWorkMonthResponseDto(WorkMonthQueryDto workMonthQueryDto) {
        return WorkMonthResponseDto.builder().id(workMonthQueryDto.id())
                .userId(workMonthQueryDto.userId()).yearMonth(workMonthQueryDto.date())
                .totalHours(convertWorkMonthHourResponseDto(workMonthQueryDto.totalHours()))
                .workDays(workMonthQueryDto.workDays().stream().map(this::convertWorkDayResponseDto).collect(Collectors.toSet()))
                .build();
    }

    public WorkDayResponseDto convertWorkDayResponseDto(WorkDayQueryDto workDayQueryDto) {
        return WorkDayResponseDto.builder().date(workDayQueryDto.date())
                .startingHour(convertWorkDayHourResponseDto(workDayQueryDto.startingHour()))
                .endingHour(convertWorkDayHourResponseDto(workDayQueryDto.endingHour()))
                .isLeave(workDayQueryDto.isLeave()).build();
    }

    public WorkMonthHourResponseDto convertWorkMonthHourResponseDto(WorkHourQueryDto workHourQueryDto) {
        return WorkMonthHourResponseDto.builder().hours(workHourQueryDto.hour()).minutes(workHourQueryDto.minutes()).build();
    }

    public WorkDayHourResponseDto convertWorkDayHourResponseDto(WorkHourQueryDto workHourQueryDto) {
        return WorkDayHourResponseDto.builder().hour(workHourQueryDto.hour()).minute(workHourQueryDto.minutes()).build();
    }
}