package com.github.schedule.workmonth;

import com.github.schedule.workmonth.vo.WorkDay;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("dev")
class WorkMonthEntityConverterTest {

    private static WorkMonthEntityConverter workMonthEntityConverter;

    @BeforeAll
    static void set() {
        workMonthEntityConverter = new WorkMonthEntityConverter();
    }

    @Test
    @DisplayName("Should Convert WorkDayEntity to WorkDay Properly")
    void shouldConvertWorkDayEntityToWorkDayProperly() {
        final LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.now());
        final WorkDayEntity workDayEntity = WorkDayEntity.builder().id(UUID.randomUUID()).date(LocalDate.now())
                                                                   .startingHour(localDateTime).endingHour(localDateTime)
                                                                   .isLeave(false).workMonth(null).build();

        final WorkDay workDay = workMonthEntityConverter.convertWorkDay(workDayEntity);
        assertEquals(workDay.date(), workDayEntity.getDate());
        assertEquals(workDay.isLeave(), workDayEntity.isLeave());
        assertEquals(workDay.startingHour().hours(), workDayEntity.getStartingHour().getHour());
        assertEquals(workDay.startingHour().minutes(), workDayEntity.getStartingHour().getMinute());
        assertEquals(workDay.endingHour().hours(), workDayEntity.getEndingHour().getHour());
        assertEquals(workDay.endingHour().minutes(), workDayEntity.getEndingHour().getMinute());
    }

}
