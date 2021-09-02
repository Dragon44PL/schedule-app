package com.github.schedule.workmonth.vo;

import com.github.schedule.workmonth.exception.WorkHourInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class WorkDayTest {

    private final LocalDate WORKDAY_DATE = LocalDate.now();
    private final boolean WORKDAY_IS_LEAVE = false;

    /*
        WorkDay Exception
     */

    private final Class<WorkHourInvalidException> WORK_HOUR_INVALID_EXCEPTION = WorkHourInvalidException.class;

    @Test
    @DisplayName("Should Create WorkDay With Dates As Zeros")
    void shouldCreateWorkDayWithZeros() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);

        assertEquals(WORKDAY_DATE, workDay.date());
        assertEquals(WORKDAY_IS_LEAVE, workDay.isLeave());
        assertEquals(WorkHour.zero(), workDay.startingHour());
        assertEquals(WorkHour.zero(), workDay.endingHour());
    }

    @Test
    @DisplayName("Should Not Create WorkDay When 'startingHour' Is Lower Than Minimal")
    void shouldNotCreateWorkDayWhenStartingHourIsLowerThanMinimal() {
        final WorkHour lowerHour = new WorkHour(WorkDay.STARTING_HOUR.hours() - 1, WorkDay.STARTING_HOUR.minutes());
        assertThrows(WORK_HOUR_INVALID_EXCEPTION, () -> new WorkDay(WORKDAY_DATE, lowerHour, WorkDay.ENDING_HOUR, WORKDAY_IS_LEAVE));
    }

    @Test
    @DisplayName("Should Not Create WorkDay When 'startingHour' Is Greater Than Maximum")
    void shouldNotCreateWorkDayWhenStartingHourIsGreaterThanMaximum() {
        final WorkHour higherHour = new WorkHour(WorkDay.ENDING_HOUR.hours() + 1, WorkDay.ENDING_HOUR.minutes());
        assertThrows(WORK_HOUR_INVALID_EXCEPTION, () -> new WorkDay(WORKDAY_DATE, higherHour, WorkDay.ENDING_HOUR, WORKDAY_IS_LEAVE));
    }

    @Test
    @DisplayName("Should Not Create WorkDay When 'endingHour' Is Lower Than Minimal")
    void shouldNotCreateWorkDayWhenEndingHourIsLowerThanMinimal() {
        final WorkHour lowerHour = new WorkHour(WorkDay.STARTING_HOUR.hours() - 1, WorkDay.STARTING_HOUR.minutes());
        assertThrows(WORK_HOUR_INVALID_EXCEPTION, () -> new WorkDay(WORKDAY_DATE, WorkDay.STARTING_HOUR, lowerHour, WORKDAY_IS_LEAVE));
    }

    @Test
    @DisplayName("Should Not Create WorkDay When 'endingHour' Is Greater Than Maximum")
    void shouldNotCreateWorkDayWhenEndingHourIsGreaterThanMaximum() {
        final WorkHour higherHour = new WorkHour(WorkDay.ENDING_HOUR.hours() + 1, WorkDay.ENDING_HOUR.minutes());
        assertThrows(WORK_HOUR_INVALID_EXCEPTION, () -> new WorkDay(WORKDAY_DATE, WorkDay.STARTING_HOUR, higherHour, WORKDAY_IS_LEAVE));
    }

    @Test
    @DisplayName("Should Calculate Total Time Properly")
    void shouldCalculateTotalTimeProperly() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WorkDay.STARTING_HOUR, WorkDay.ENDING_HOUR, WORKDAY_IS_LEAVE);
        final WorkHour calculated = workDay.calculateTotalTime();

        assertEquals(WorkDay.ENDING_HOUR.hours() - WorkDay.STARTING_HOUR.hours(), calculated.hours());
        assertEquals(WorkDay.ENDING_HOUR.minutes() - WorkDay.STARTING_HOUR.minutes(), calculated.minutes());
    }

    @Test
    @DisplayName("Should Calculate Total Time Properly When Zeros")
    void shouldCalculateTotalTimeProperlyWhenZeros() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        final WorkHour calculated = workDay.calculateTotalTime();

        assertEquals(0, calculated.hours());
        assertEquals(0, calculated.minutes());
    }

    @Test
    @DisplayName("WorkDays Dates Should Be The Same")
    void shouldWorkDaysDatesBeTheSame() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        assertEquals(WORKDAY_DATE, workDay.date());
    }

    @Test
    @DisplayName("WorkDays Dates Should Not Be The Same")
    void shouldWorkDaysDatesNotBeTheSame() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        assertNotEquals(WORKDAY_DATE.plusDays(1), workDay.date());
    }

    @Test
    @DisplayName("WorkDays Date Should Be Not Lesser Than")
    void workDaysDateShouldBeNotLesserThan() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        assertTrue(workDay.notLesserThan(WORKDAY_DATE));
        assertTrue(workDay.notLesserThan(WORKDAY_DATE.minusDays(1)));
    }

    @Test
    @DisplayName("WorkDays Dates Should Be Lesser Than")
    void workDaysDateShouldBeLesserThan() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        assertFalse(workDay.notLesserThan(WORKDAY_DATE.plusDays(1)));
    }

    @Test
    @DisplayName("WorkDays Date Should Be Not Greater Than")
    void workDaysDateShouldBeNotGreaterThan() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        assertTrue(workDay.notGreaterThan(WORKDAY_DATE));
        assertTrue(workDay.notGreaterThan(WORKDAY_DATE.plusDays(1)));
    }

    @Test
    @DisplayName("WorkDays Dates Should Be Greater Than")
    void workDaysDateShouldBeGreaterThan() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        assertFalse(workDay.notGreaterThan(WORKDAY_DATE.minusDays(1)));
    }

    @Test
    @DisplayName("'contentChanged' Should Be False When Nothing Changed")
    void workDaysChangedShouldBeFalseWhenNothingChanged() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        assertFalse(workDay.contentChanged(workDay));
    }

    @Test
    @DisplayName("'contentChanged' Should Be True When 'isLeave' Changed")
    void workDaysChangedShouldBeTrueWhenIsLeaveChanged() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        final WorkDay another = new WorkDay(WORKDAY_DATE, !WORKDAY_IS_LEAVE);
        assertTrue(workDay.contentChanged(another));
    }

    @Test
    @DisplayName("'contentChanged' Should Be True When 'startingHour' Changed")
    void workDaysChangedShouldBeTrueWhenStartingHourChanged() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        final WorkDay another = new WorkDay(WORKDAY_DATE, new WorkHour(1, 0), new WorkHour(0, 0), WORKDAY_IS_LEAVE);
        assertTrue(workDay.contentChanged(another));
    }

    @Test
    @DisplayName("'contentChanged' Should Be True When 'endingHour' Changed")
    void workDaysChangedShouldBeTrueWhenEndingHourChanged() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        final WorkDay another = new WorkDay(WORKDAY_DATE, new WorkHour(0, 0), new WorkHour(1, 0), WORKDAY_IS_LEAVE);
        assertTrue(workDay.contentChanged(another));
    }

    @Test
    @DisplayName("'contentChanged' Should Be True When Both Hours Changed")
    void workDaysChangedShouldBeTrueWhenBothHoursChanged() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        final WorkDay another = new WorkDay(WORKDAY_DATE, new WorkHour(1, 0), new WorkHour(1, 0), WORKDAY_IS_LEAVE);
        assertTrue(workDay.contentChanged(another));
    }

    @Test
    @DisplayName("'Changed' Should Be True When Both Hours Changed")
    void workDaysChangedShouldBeTrueWhenEverythingChanged() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        final WorkDay another = new WorkDay(WORKDAY_DATE, new WorkHour(1, 0), new WorkHour(1, 0), !WORKDAY_IS_LEAVE);
        assertTrue(workDay.contentChanged(another));
    }

    @Test
    @DisplayName("'sameDate' Should Be True When Both Dates Are Same")
    void sameDateShouldBeTrueWhenBothDatesAreSame() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        assertTrue(workDay.sameDate(WORKDAY_DATE));
    }

    @Test
    @DisplayName("'sameDate' Should Be False When Both Dates Are Not Same")
    void sameDateShouldBeFalseWhenBothDatesAreNotSame() {
        final WorkDay workDay = new WorkDay(WORKDAY_DATE, WORKDAY_IS_LEAVE);
        assertFalse(workDay.sameDate(WORKDAY_DATE.plusDays(1)));
    }

}
