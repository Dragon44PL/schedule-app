package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkDayQueryDto;
import com.github.schedule.workmonth.dto.WorkHourQueryDto;
import com.github.schedule.workmonth.dto.WorkMonthQueryDto;

import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class FlattenedWorkMonthConverter {

    static List<WorkMonthQueryDto> convertFlattenedResponse(List<FlattenedWorkMonthCollection> collection) {
        return collection.stream().collect(Collectors.groupingBy(FlattenedWorkMonthCollection::getId))
                .values()
                .stream()
                .map(FlattenedWorkMonthConverter::createWorkMonthQueryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static WorkMonthQueryDto createWorkMonthQueryDto(List<FlattenedWorkMonthCollection> collections) {
        final Set<WorkDayQueryDto> workDays = collections.stream()
                .map(FlattenedWorkMonthCollection::toWorkDayDto)
                .filter(FlattenedWorkMonthConverter::nonEmpty)
                .collect(Collectors.toSet());
        return collections.size() != 0 ? createSingleWorkMontQueryDto(workDays, collections.stream().findAny().get()) : null;
    }

    private static WorkMonthQueryDto createSingleWorkMontQueryDto(Set<WorkDayQueryDto> workDays, FlattenedWorkMonthCollection collection) {
        return new WorkMonthQueryDto(
                collection.getId(),
                collection.getUserId(),
                YearMonth.of(collection.getYear(), collection.getMonth()),
                new WorkHourQueryDto(collection.getHours(), collection.getMinutes()),
                workDays
        );
    }

    private static boolean nonEmpty(WorkDayQueryDto workDayQueryDto) {
        return Stream.of(workDayQueryDto, workDayQueryDto.date(), workDayQueryDto.isLeave(), workDayQueryDto.startingHour(), workDayQueryDto.endingHour()).allMatch(Objects::nonNull);
    }
}
