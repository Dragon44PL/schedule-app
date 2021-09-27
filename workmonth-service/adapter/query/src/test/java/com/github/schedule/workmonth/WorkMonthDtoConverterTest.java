package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkDayQueryDto;
import com.github.schedule.workmonth.dto.WorkHourQueryDto;
import com.github.schedule.workmonth.dto.WorkMonthQueryDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkMonthDtoConverterTest {

    private static WorkMonthDtoConverter workMonthDtoConverter;

    @BeforeAll
    static void set() {
        workMonthDtoConverter = new WorkMonthDtoConverter();
    }

    @Test
    @DisplayName("Should Create WorkHourQueryDto From LocalDateTime Properly")
    void shouldCreateWorkHourDtoFromLocalDateTimeProperly() {
        final LocalDateTime localDateTime = LocalDateTime.now();
        final WorkHourQueryDto workHourQueryDto = workMonthDtoConverter.workHourDto(localDateTime);

        assertEquals(localDateTime.getHour(), workHourQueryDto.hour());
        assertEquals(localDateTime.getMinute(), workHourQueryDto.minutes());
    }

    @Test
    @DisplayName("Should Create WorkHourQueryDto Properly")
    void shouldCreateWorkHourQueryDtoProperly() {
        final LocalTime localTime = LocalTime.now();
        final WorkHourEntity workHourEntity = WorkHourEntity.builder().hours(localTime.getHour()).minutes(localTime.getMinute()).build();
        final WorkHourQueryDto workHourQueryDto = workMonthDtoConverter.workHourDto(workHourEntity);

        assertEquals(workHourEntity.getHours(), workHourQueryDto.hour());
        assertEquals(workHourEntity.getMinutes(), workHourQueryDto.minutes());
    }

    @Test
    @DisplayName("Should Create WorkDayQueryDto Properly")
    void shouldCreateWorkHourDtoProperly() {
        final LocalDateTime first = LocalDateTime.now();
        final LocalDateTime second = first.plusHours(4).plusMinutes(30);

        final WorkDayEntity workDayEntity = WorkDayEntity.builder().id(UUID.randomUUID()).date(LocalDate.now()).startingHour(first).endingHour(second).isLeave(false).build();
        final WorkDayQueryDto workDayQueryDto = workMonthDtoConverter.workDayDto(workDayEntity);

        assertEquals(workDayEntity.getDate(), workDayQueryDto.date());
        assertEquals(workDayEntity.isLeave(), workDayQueryDto.isLeave());
        assertEquals(workDayEntity.getStartingHour().getHour(), workDayQueryDto.startingHour().hour());
        assertEquals(workDayEntity.getStartingHour().getMinute(), workDayQueryDto.startingHour().minutes());
        assertEquals(workDayEntity.getEndingHour().getHour(), workDayQueryDto.endingHour().hour());
        assertEquals(workDayEntity.getEndingHour().getMinute(), workDayQueryDto.endingHour().minutes());
    }

    @Test
    @DisplayName("Should Create WorkMonthQueryDto Properly")
    void shouldCreateWorkMonthQueryDtoProperly() {
        final LocalDateTime first = LocalDateTime.now();
        final LocalDateTime second = first.plusHours(4).plusMinutes(30);
        final WorkDayEntity workDayEntity = WorkDayEntity.builder().id(UUID.randomUUID()).date(LocalDate.now()).startingHour(first).endingHour(second).isLeave(false).build();

        final LocalTime localTime = LocalTime.now();
        final WorkHourEntity workHourEntity = WorkHourEntity.builder().hours(localTime.getHour()).minutes(localTime.getMinute()).build();
        final WorkMonthEntity workMonthEntity = WorkMonthEntity.builder().id(UUID.randomUUID()).userId(UUID.randomUUID()).date(YearMonth.now())
                                                               .workDays(Set.of(workDayEntity)).totalHours(workHourEntity).build();

        final WorkMonthQueryDto workMonthQueryDto = workMonthDtoConverter.workMonthDto(workMonthEntity);

        assertEquals(workMonthEntity.getId(), workMonthQueryDto.id());
        assertEquals(workMonthEntity.getUserId(), workMonthQueryDto.userId());
        assertEquals(workMonthEntity.getDate(), workMonthQueryDto.date());
        assertEquals(workMonthEntity.getTotalHours().getHours(), workMonthQueryDto.totalHours().hour());
        assertEquals(workMonthEntity.getTotalHours().getMinutes(), workMonthQueryDto.totalHours().minutes());
        assertEquals(1, workMonthQueryDto.workDays().size());

        final WorkDayQueryDto workDayQueryDto = workMonthQueryDto.workDays().iterator().next();
        assertEquals(workDayEntity.getDate(), workDayQueryDto.date());
        assertEquals(workDayEntity.isLeave(), workDayQueryDto.isLeave());
        assertEquals(workDayEntity.getStartingHour().getHour(), workDayQueryDto.startingHour().hour());
        assertEquals(workDayEntity.getStartingHour().getMinute(), workDayQueryDto.startingHour().minutes());
        assertEquals(workDayEntity.getEndingHour().getHour(), workDayQueryDto.endingHour().hour());
        assertEquals(workDayEntity.getEndingHour().getMinute(), workDayQueryDto.endingHour().minutes());
    }

}
