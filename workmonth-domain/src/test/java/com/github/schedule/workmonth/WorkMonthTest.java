package com.github.schedule.workmonth;

import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.event.WorkDayUpdatedEvent;
import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.exception.WorkDayInvalidException;
import com.github.schedule.workmonth.vo.WorkDate;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WorkMonthTest {

    private final UUID WORK_MONTH_ID = UUID.randomUUID();
    private final WorkDate WORKDATE = WorkDate.of(LocalDate.now());

    /*
        WorkMonth Events
     */

    private final Class<WorkMonthCreatedEvent> WORK_MONTH_CREATED_EVENT = WorkMonthCreatedEvent.class;
    private final Class<WorkDayUpdatedEvent> WORK_DAY_UPDATED_EVENT = WorkDayUpdatedEvent.class;
    private final Class<TotalHoursCalculatedEvent> TOTAL_HOURS_CALCULATED_EVENT = TotalHoursCalculatedEvent.class;

    /*
        WorkMonth Exceptions
     */

    private final Class<WorkDayInvalidException> WORK_DAY_INVALID_EXCEPTION = WorkDayInvalidException.class;

    @Test
    @DisplayName("WorkMonth Should Be Restored Properly")
    void workMonthShouldBeRestoredProperly() {
        final Set<WorkDay> workDays = new HashSet<>();
        final WorkMonth workMonth = WorkMonth.restore(WORK_MONTH_ID, WORKDATE, workDays);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();

        assertTrue(workMonthEvent.isEmpty());
    }

    @Test
    @DisplayName("WorkMonth Should Be Restored Properly With Events And Empty")
    void workMonthShouldBeRestoredProperlyWithEventsAndEmpty() {
        final List<WorkMonthEvent> events = new ArrayList<>();
        final Set<WorkDay> workDays = new HashSet<>();
        final WorkMonth workMonth = WorkMonth.restore(WORK_MONTH_ID, WORKDATE, workDays, events);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();

        assertTrue(workMonthEvent.isEmpty());
    }

    @Test
    @DisplayName("WorkMonth Should Be Restored Properly With Events")
    void workMonthShouldBeRestoredProperlyWithEvents() {
        final WorkHour totalHours = new WorkHour(10, 30);
        final List<WorkMonthEvent> events = Collections.singletonList(new TotalHoursCalculatedEvent(WORK_MONTH_ID, totalHours));
        final Set<WorkDay> workDays = new HashSet<>();
        final WorkMonth workMonth = WorkMonth.restore(WORK_MONTH_ID, WORKDATE, workDays, events);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertFalse(workMonthEvent.isEmpty());
        assertEquals(TOTAL_HOURS_CALCULATED_EVENT, workMonthEvent.get().getClass());

        final TotalHoursCalculatedEvent totalHoursCalculatedEvent = (TotalHoursCalculatedEvent) workMonthEvent.get();
        assertEquals(WORK_MONTH_ID, totalHoursCalculatedEvent.aggregateId());
        assertEquals(totalHours, totalHoursCalculatedEvent.totalWorkHours());
    }

    @Test
    @DisplayName("WorkMonth Should Be Created Properly")
    void workMonthShouldBeCreatedProperly() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, WORKDATE);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertEquals(WORK_MONTH_CREATED_EVENT, workMonthEvent.get().getClass());

        final WorkMonthCreatedEvent workMonthCreatedEvent = (WorkMonthCreatedEvent) workMonthEvent.get();
        final LocalDate startingDate = LocalDate.of(WORKDATE.year(), WORKDATE.month(), 1);
        final LocalDate endingDate = startingDate.withDayOfMonth(startingDate.lengthOfMonth());
        assertEquals(WORK_MONTH_ID, workMonthCreatedEvent.aggregateId());
        assertEquals(startingDate, workMonthCreatedEvent.startingDate());
        assertEquals(endingDate, workMonthCreatedEvent.endingDate());
        assertEquals(WorkHour.zero(), workMonthCreatedEvent.totalHours());
        assertEquals(endingDate.lengthOfMonth(), workMonthCreatedEvent.workDays().size());

        final LocalDate currentDate = LocalDate.of(WORKDATE.year(), WORKDATE.month(), 1);
        currentDate.datesUntil(currentDate.plusDays(1)).forEach((date) -> {
            final Optional<WorkDay> workDay = workMonthCreatedEvent.workDays().stream().filter((day) -> day.same(date)).findAny();
            assertTrue(workDay.isPresent());
            assertEquals(WorkDay.STARTING_HOUR, workDay.get().startingHour());
            assertEquals(WorkDay.STARTING_HOUR, workDay.get().endingHour());
        });
    }

    @Test
    @DisplayName("WorkMonth Should Change WorkDay When Exists In Current WorkMonth Range")
    void shouldChangeWorkDayWhenExists() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, WORKDATE);

        final WorkHour startingHour = new WorkHour(8, 0);
        final WorkHour endingHour = new WorkHour(16, 0);
        final WorkDay workDay = new WorkDay(LocalDate.of(WORKDATE.year(), WORKDATE.month(), 1).plusDays(10), startingHour, endingHour, false);
        workMonth.changeWorkDay(workDay);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertEquals(WORK_DAY_UPDATED_EVENT, workMonthEvent.get().getClass());

        final WorkDayUpdatedEvent workDayUpdatedEvent = (WorkDayUpdatedEvent) workMonthEvent.get();
        assertEquals(WORK_MONTH_ID, workDayUpdatedEvent.aggregateId());
        assertEquals(workDay, workDayUpdatedEvent.workDay());
    }

    @Test
    @DisplayName("WorkMonth Not Should Change WorkDay When Haven't Changed")
    void shouldNotChangeWorkDayWhenHaveNotChanged() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, WORKDATE);
        final WorkDay workDay = new WorkDay(LocalDate.of(WORKDATE.year(), WORKDATE.month(), 1).plusDays(10), false);
        workMonth.changeWorkDay(workDay);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertNotEquals(WORK_DAY_UPDATED_EVENT, workMonthEvent.get().getClass());
    }

    @Test
    @DisplayName("WorkMonth Should Throws 'WorkDayInvalidException' When WorkDay Not In Range")
    void shouldThrowsWorkDayInvalidExceptionWhenWorkDayNotInRange() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, WORKDATE);

        final LocalDate startingDate = LocalDate.of(WORKDATE.year(), WORKDATE.month(), 1);
        final WorkDay workDayWithDayBefore = new WorkDay(startingDate.minusDays(1), false);
        assertThrows(WORK_DAY_INVALID_EXCEPTION, () -> workMonth.changeWorkDay(workDayWithDayBefore));

        final LocalDate endingDate = startingDate.withDayOfMonth(startingDate.lengthOfMonth()).plusDays(1);
        final WorkDay workDayWithDayAfter = new WorkDay(endingDate, false);
        assertThrows(WORK_DAY_INVALID_EXCEPTION, () -> workMonth.changeWorkDay(workDayWithDayAfter));
    }

    @Test
    @DisplayName("WorkMonth Should Calculate Zero Hours When Not Changing WorkDays")
    void shouldCalculateZeroHoursWhenNotChangingWorkDays() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, WORKDATE);
        workMonth.calculateTotalHours();

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertEquals(TOTAL_HOURS_CALCULATED_EVENT, workMonthEvent.get().getClass());

        final TotalHoursCalculatedEvent totalHoursCalculatedEvent = (TotalHoursCalculatedEvent) workMonthEvent.get();
        assertEquals(WORK_MONTH_ID, totalHoursCalculatedEvent.aggregateId());
        assertEquals(0, totalHoursCalculatedEvent.totalWorkHours().hours());
        assertEquals(0, totalHoursCalculatedEvent.totalWorkHours().minutes());
    }

    @Test
    @DisplayName("WorkMonth Should Calculate Total Hours Properly When WorkDay Changed")
    void shouldCalculateTotalHoursProperlyWhenWorkDayChanged() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, WORKDATE);

        final WorkHour first = new WorkHour(8, 30);
        final WorkHour second = new WorkHour(17, 0);
        final WorkDay workDay = new WorkDay(LocalDate.of(WORKDATE.year(), WORKDATE.month(), 1), first, second, false);

        workMonth.changeWorkDay(workDay);
        workMonth.calculateTotalHours();
        final WorkHour calculated = first.difference(second);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertEquals(TOTAL_HOURS_CALCULATED_EVENT, workMonthEvent.get().getClass());

        final TotalHoursCalculatedEvent totalHoursCalculatedEvent = (TotalHoursCalculatedEvent) workMonthEvent.get();
        assertEquals(WORK_MONTH_ID, totalHoursCalculatedEvent.aggregateId());
        assertEquals(calculated, totalHoursCalculatedEvent.totalWorkHours());
    }

    @Test
    @DisplayName("WorkMonth Should Not Include Changed WorkDay When 'isLeave' is true")
    void shouldNotIncludeChangedWorkDayWhenIsLeaveIsTrue() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, WORKDATE);

        final WorkHour first = new WorkHour(8, 30);
        final WorkHour second = new WorkHour(17, 0);
        final WorkDay workDay = new WorkDay(LocalDate.of(WORKDATE.year(), WORKDATE.month(), 1), first, second, true);

        workMonth.changeWorkDay(workDay);
        workMonth.calculateTotalHours();
        final WorkHour calculated = first.difference(second);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertEquals(TOTAL_HOURS_CALCULATED_EVENT, workMonthEvent.get().getClass());

        final TotalHoursCalculatedEvent totalHoursCalculatedEvent = (TotalHoursCalculatedEvent) workMonthEvent.get();
        assertEquals(WORK_MONTH_ID, totalHoursCalculatedEvent.aggregateId());
        assertNotEquals(calculated, totalHoursCalculatedEvent.totalWorkHours());
        assertEquals(WorkHour.zero(), totalHoursCalculatedEvent.totalWorkHours());
    }
}