package com.github.schedule.workmonth.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkHourTest {

    private final int LESSER_HOUR = 15;
    private final int GREATER_HOUR = 16;

    private final int LESSER_MINUTE = 30;
    private final int GREATER_MINUTE = 50;

    @Test
    @DisplayName("WorkHour Should Be Created Properly")
    void workHourShouldCreateProperly() {
        final WorkHour workHour = new WorkHour(LESSER_HOUR, LESSER_MINUTE);

        assertEquals(LESSER_HOUR, workHour.hours());
        assertEquals(LESSER_MINUTE, workHour.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Calculate Difference Properly When First WorkHour Is Zero")
    void workHourShouldCalculateDifferenceProperlyWhenFirstWorkHourIsZero() {
        final WorkHour first = WorkHour.zero();
        final WorkHour second = new WorkHour(LESSER_HOUR, 0);

        final WorkHour sum = first.difference(second);
        final WorkHour properData = new WorkHour(LESSER_HOUR, 0);

        assertEquals(properData.hours(), sum.hours());
        assertEquals(properData.minutes(), sum.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Calculate Difference Properly When Second WorkHour Is Zero")
    void workHourShouldCalculateDifferenceProperlyWhenSecondWorkHourIsZero() {
        final WorkHour first = new WorkHour(LESSER_HOUR, 0);
        final WorkHour second = WorkHour.zero();

        final WorkHour sum = first.difference(second);
        final WorkHour properData = new WorkHour(LESSER_HOUR, 0);

        assertEquals(properData.hours(), sum.hours());
        assertEquals(properData.minutes(), sum.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Calculate Difference Properly When WorkHour Is Zero")
    void workHourShouldCalculateDifferenceProperlyWhenWorkHourIsZero() {
        final WorkHour first = WorkHour.zero();
        final WorkHour second = WorkHour.zero();

        final WorkHour sum = first.difference(second);
        final WorkHour properData = WorkHour.zero();

        assertEquals(properData.hours(), sum.hours());
        assertEquals(properData.minutes(), sum.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Count Difference Properly When Same Hour And Minutes")
    void workHourShouldCountDifferenceProperlyWhenSameHourAndMinutes() {
        final WorkHour first = new WorkHour(GREATER_HOUR, GREATER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, GREATER_MINUTE);

        final WorkHour difference = first.difference(second);
        final WorkHour properData = new WorkHour(0, 0);

        assertEquals(properData.hours(), difference.hours());
        assertEquals(properData.minutes(), difference.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Count Difference Properly When Same Hour And Minutes When Triggered On Second")
    void workHourShouldCountDifferenceProperlyWhenSameHourAndMinutesWhenTriggeredOnSecond() {
        final WorkHour first = new WorkHour(GREATER_HOUR, GREATER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, GREATER_MINUTE);

        final WorkHour difference = second.difference(first);
        final WorkHour properData = new WorkHour(0, 0);

        assertEquals(properData.hours(), difference.hours());
        assertEquals(properData.minutes(), difference.minutes());
    }

    // MINUTES DIFFERENT

    @Test
    @DisplayName("WorkHour Should Count Difference Properly When Same Hour But Minutes Greater")
    void workHourShouldCountDifferenceProperlyWhenSameHourButMinutesGreater() {
        final WorkHour first = new WorkHour(GREATER_HOUR, GREATER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, LESSER_MINUTE);

        final WorkHour difference = first.difference(second);
        final WorkHour properData = new WorkHour(0, GREATER_MINUTE - LESSER_MINUTE);

        assertEquals(properData.hours(), difference.hours());
        assertEquals(properData.minutes(), difference.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Count Difference Properly When Same Hour But Minutes Greater When Triggered On Second")
    void workHourShouldCountDifferenceProperlyWhenSameHourButMinutesGreaterWhenTriggeredOnSecond() {
        final WorkHour first = new WorkHour(GREATER_HOUR, GREATER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, LESSER_MINUTE);

        final WorkHour difference = second.difference(first);
        final WorkHour properData = new WorkHour(0, GREATER_MINUTE - LESSER_MINUTE);

        assertEquals(properData.hours(), difference.hours());
        assertEquals(properData.minutes(), difference.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Count Difference Properly When Same Hour But Minutes Smaller")
    void workHourShouldCountDifferenceProperlyWhenSameHourButMinutesSmaller() {
        final WorkHour first = new WorkHour(GREATER_HOUR, LESSER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, GREATER_MINUTE);

        final WorkHour difference = first.difference(second);
        final WorkHour properData = new WorkHour(0, GREATER_MINUTE - LESSER_MINUTE);

        assertEquals(properData.hours(), difference.hours());
        assertEquals(properData.minutes(), difference.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Count Difference Properly When Same Hour But Minutes Smaller When Triggered On Second")
    void workHourShouldCountDifferenceProperlyWhenSameHourButMinutesSmallerWhenTriggeredOnSecond() {
        final WorkHour first = new WorkHour(GREATER_HOUR, LESSER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, GREATER_MINUTE);

        final WorkHour difference = second.difference(first);
        final WorkHour properData = new WorkHour(0, GREATER_MINUTE - LESSER_MINUTE);

        assertEquals(properData.hours(), difference.hours());
        assertEquals(properData.minutes(), difference.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Count Difference Properly When Smaller Hour")
    void workHourCountDifferenceProperlyWhenSmallerHour() {
        final WorkHour first = new WorkHour(LESSER_HOUR, LESSER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, LESSER_MINUTE);

        final WorkHour difference = first.difference(second);
        final WorkHour properData = new WorkHour(GREATER_HOUR - LESSER_HOUR, 0);

        assertEquals(properData.hours(), difference.hours());
        assertEquals(properData.minutes(), difference.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Count Difference Properly When Smaller Hour When Triggered On Second")
    void workHourCountDifferenceProperlyWhenSmallerHourWhenTriggeredOnSecond() {
        final WorkHour first = new WorkHour(LESSER_HOUR, LESSER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, LESSER_MINUTE);

        final WorkHour difference = second.difference(first);
        final WorkHour properData = new WorkHour(GREATER_HOUR - LESSER_HOUR, 0);

        assertEquals(properData.hours(), difference.hours());
        assertEquals(properData.minutes(), difference.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Count Difference Properly When Greater Hour")
    void workHourCountDifferenceProperlyWhenGreaterHour() {
        final WorkHour first = new WorkHour(GREATER_HOUR, LESSER_MINUTE);
        final WorkHour second = new WorkHour(LESSER_HOUR, LESSER_MINUTE);

        final WorkHour difference = first.difference(second);
        final WorkHour properData = new WorkHour(GREATER_HOUR - LESSER_HOUR, 0);

        assertEquals(properData.hours(), difference.hours());
        assertEquals(properData.minutes(), difference.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Count Difference Properly When Greater Hour When Triggered On Second")
    void workHourCountDifferenceProperlyWhenGreaterHourWhenTriggeredOnSecond() {
        final WorkHour first = new WorkHour(GREATER_HOUR, LESSER_MINUTE);
        final WorkHour second = new WorkHour(LESSER_HOUR, LESSER_MINUTE);

        final WorkHour difference = second.difference(first);
        final WorkHour properData = new WorkHour(GREATER_HOUR - LESSER_HOUR, 0);

        assertEquals(properData.hours(), difference.hours());
        assertEquals(properData.minutes(), difference.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Calculate Sum Properly When Minutes Sum Is Greater Than 60")
    void workHourShouldCalculateSumProperlyWhenMinutesGreaterThanAllowed() {
        final WorkHour first = new WorkHour(GREATER_HOUR, GREATER_MINUTE);
        final WorkHour second = new WorkHour(LESSER_HOUR, GREATER_MINUTE);

        final WorkHour sum = first.add(second);
        final WorkHour properData = new WorkHour(GREATER_HOUR + LESSER_HOUR + ((GREATER_MINUTE + GREATER_MINUTE) / 60), (GREATER_MINUTE + GREATER_MINUTE) % 60);

        assertEquals(properData.hours(), sum.hours());
        assertEquals(properData.minutes(), sum.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Calculate Sum Properly When Minutes Sum Is Smaller Than 60")
    void workHourShouldCalculateSumProperlyWhenMinutesSmallerThanAllowed() {
        final WorkHour first = new WorkHour(GREATER_HOUR, LESSER_MINUTE);
        final WorkHour second = new WorkHour(LESSER_HOUR, 0);

        final WorkHour sum = first.add(second);
        final WorkHour properData = new WorkHour(GREATER_HOUR + LESSER_HOUR, LESSER_MINUTE);

        assertEquals(properData.hours(), sum.hours());
        assertEquals(properData.minutes(), sum.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Calculate Sum Properly When First WorkHour Is Zero")
    void workHourShouldCalculateSumProperlyWhenFirstWorkHourIsZero() {
        final WorkHour first = WorkHour.zero();
        final WorkHour second = new WorkHour(LESSER_HOUR, 0);

        final WorkHour sum = first.add(second);
        final WorkHour properData = new WorkHour(LESSER_HOUR, 0);

        assertEquals(properData.hours(), sum.hours());
        assertEquals(properData.minutes(), sum.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Calculate Sum Properly When Second WorkHour Is Zero")
    void workHourShouldCalculateSumProperlyWhenSecondWorkHourIsZero() {
        final WorkHour first = new WorkHour(LESSER_HOUR, 0);
        final WorkHour second = WorkHour.zero();

        final WorkHour sum = first.add(second);
        final WorkHour properData = new WorkHour(LESSER_HOUR, 0);

        assertEquals(properData.hours(), sum.hours());
        assertEquals(properData.minutes(), sum.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Calculate Sum Properly When WorkHour Is Zero")
    void workHourShouldCalculateSumProperlyWhenWorkHourIsZero() {
        final WorkHour first = WorkHour.zero();
        final WorkHour second = WorkHour.zero();

        final WorkHour sum = first.add(second);
        final WorkHour properData = WorkHour.zero();

        assertEquals(properData.hours(), sum.hours());
        assertEquals(properData.minutes(), sum.minutes());
    }

    @Test
    @DisplayName("WorkHour Should Not Be Greater Than Another When Lesser Hour And Minute")
    void workHourShouldBeNotGreaterThanSecond() {
        final WorkHour first = new WorkHour(LESSER_HOUR, LESSER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, GREATER_MINUTE);

        assertTrue(first.notGreaterThan(second));
        assertTrue(second.notLesserThan(first));
    }

    @Test
    @DisplayName("WorkHour Should Not Be Greater Than Another When Lesser Hour")
    void workHourShouldBeNotGreaterThanSecondWhenLesserHour() {
        final WorkHour first = new WorkHour(LESSER_HOUR, GREATER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, GREATER_MINUTE);

        assertTrue(first.notGreaterThan(second));
        assertTrue(second.notLesserThan(first));
    }

    @Test
    @DisplayName("WorkHour Should Not Be Greater Than Another When Lesser Minute")
    void workHourShouldBeNotGreaterThanSecondWhenLesserMinute() {
        final WorkHour first = new WorkHour(GREATER_HOUR, LESSER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, GREATER_MINUTE);

        assertTrue(first.notGreaterThan(second));
        assertTrue(second.notLesserThan(first));
    }

    @Test
    @DisplayName("WorkHour Should Not Be Greater Than Another When Same Hour And Minute")
    void workHourShouldBeNotGreaterThanSecondWhenSameHourAndMinute() {
        final WorkHour first = new WorkHour(GREATER_HOUR, GREATER_MINUTE);
        final WorkHour second = new WorkHour(GREATER_HOUR, GREATER_MINUTE);

        assertTrue(first.notGreaterThan(second));
        assertTrue(second.notLesserThan(first));
    }

    @Test
    @DisplayName("WorkHour Should Be Greater Than Another When Greater Hour And Minute")
    void workHourShouldBeGreaterThanSecondWhenGreaterHourAndMinute() {
        final WorkHour first = new WorkHour(GREATER_HOUR, GREATER_MINUTE);
        final WorkHour second = new WorkHour(LESSER_HOUR, LESSER_MINUTE);

        assertFalse(first.notGreaterThan(second));
        assertFalse(second.notLesserThan(first));
    }

    @Test
    @DisplayName("WorkHour Should Be Greater Than Another When Greater Hour")
    void workHourShouldBeGreaterThanSecondWhenGreaterHour() {
        final WorkHour first = new WorkHour(GREATER_HOUR, LESSER_MINUTE);
        final WorkHour second = new WorkHour(LESSER_HOUR, LESSER_MINUTE);

        assertFalse(first.notGreaterThan(second));
        assertFalse(second.notLesserThan(first));
    }

    @Test
    @DisplayName("WorkHour Should Be Greater Than Another When Greater Minute")
    void workHourShouldBeGreaterThanSecondWhenGreaterMinute() {
        final WorkHour first = new WorkHour(LESSER_HOUR, GREATER_MINUTE);
        final WorkHour second = new WorkHour(LESSER_HOUR, LESSER_MINUTE);

        assertFalse(first.notGreaterThan(second));
        assertFalse(second.notLesserThan(first));
    }

    @Test
    @DisplayName("WorkHour Should Not Be Greater Than Another When Same Hour And Minute")
    void workHourShouldBeGreaterThanSecondWhenSameHourAndMinute() {
        final WorkHour first = new WorkHour(LESSER_HOUR, GREATER_MINUTE);
        final WorkHour second = new WorkHour(LESSER_HOUR, GREATER_MINUTE);

        assertTrue(first.notGreaterThan(second));
        assertTrue(second.notLesserThan(first));
    }
}
