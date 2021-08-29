package com.github.schedule.workmonth;

import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.event.WorkDaysChangedEvent;
import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.exception.WorkDayInvalidException;
import com.github.schedule.workmonth.vo.UserId;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WorkMonthTest {

    private final UUID WORK_MONTH_ID = UUID.randomUUID();
    private final UserId USER_ID = new UserId(UUID.randomUUID());
    private final YearMonth YEAR_MONTH = YearMonth.now();

    /*
        WorkMonth Events
     */

    private final Class<WorkMonthCreatedEvent> WORK_MONTH_CREATED_EVENT = WorkMonthCreatedEvent.class;
    private final Class<WorkDaysChangedEvent> WORK_DAYS_CHANGED_EVENT = WorkDaysChangedEvent.class;
    private final Class<TotalHoursCalculatedEvent> TOTAL_HOURS_CALCULATED_EVENT = TotalHoursCalculatedEvent.class;

    /*
        WorkMonth Exceptions
     */

    private final Class<WorkDayInvalidException> WORK_DAY_INVALID_EXCEPTION = WorkDayInvalidException.class;

    @Test
    @DisplayName("WorkMonth Should Be Restored Properly")
    void workMonthShouldBeRestoredProperly() {
        final Set<WorkDay> workDays = new HashSet<>();
        final WorkMonth workMonth = WorkMonth.restore(WORK_MONTH_ID, USER_ID, YEAR_MONTH, workDays);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();

        assertTrue(workMonthEvent.isEmpty());
    }

    @Test
    @DisplayName("WorkMonth Should Be Restored Properly With Events And Empty")
    void workMonthShouldBeRestoredProperlyWithEventsAndEmpty() {
        final List<WorkMonthEvent> events = new ArrayList<>();
        final Set<WorkDay> workDays = new HashSet<>();
        final WorkMonth workMonth = WorkMonth.restore(WORK_MONTH_ID, USER_ID, YEAR_MONTH, workDays, events);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();

        assertTrue(workMonthEvent.isEmpty());
    }

    @Test
    @DisplayName("WorkMonth Should Be Restored Properly With Events")
    void workMonthShouldBeRestoredProperlyWithEvents() {
        final WorkHour totalHours = new WorkHour(10, 30);
        final List<WorkMonthEvent> events = Collections.singletonList(new TotalHoursCalculatedEvent(WORK_MONTH_ID, totalHours));
        final Set<WorkDay> workDays = new HashSet<>();
        final WorkMonth workMonth = WorkMonth.restore(WORK_MONTH_ID, USER_ID, YEAR_MONTH, workDays, events);

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
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YEAR_MONTH);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertEquals(WORK_MONTH_CREATED_EVENT, workMonthEvent.get().getClass());

        final WorkMonthCreatedEvent workMonthCreatedEvent = (WorkMonthCreatedEvent) workMonthEvent.get();
        assertEquals(WORK_MONTH_ID, workMonthCreatedEvent.aggregateId());
        assertEquals(USER_ID, workMonthCreatedEvent.userId());
        assertEquals(YEAR_MONTH, workMonthCreatedEvent.yearMonth());
        assertEquals(WorkHour.zero(), workMonthCreatedEvent.totalHours());
        assertEquals(YEAR_MONTH.lengthOfMonth(), workMonthCreatedEvent.workDays().size());

        final LocalDate currentDate = YEAR_MONTH.atDay(1);
        currentDate.datesUntil(currentDate.plusDays(1)).forEach((date) -> {
            final Optional<WorkDay> workDay = workMonthCreatedEvent.workDays().stream().filter((day) -> day.sameDate(date)).findAny();
            assertTrue(workDay.isPresent());
            assertEquals(WorkDay.STARTING_HOUR, workDay.get().startingHour());
            assertEquals(WorkDay.STARTING_HOUR, workDay.get().endingHour());
        });
    }

    @Test
    @DisplayName("WorkMonth Should Change WorkDay When Exists In Current WorkMonth Range")
    void shouldChangeWorkDayWhenExists() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YEAR_MONTH);

        final WorkHour startingHour = new WorkHour(8, 0);
        final WorkHour endingHour = new WorkHour(16, 0);
        final WorkDay workDay = new WorkDay(YEAR_MONTH.atDay(10), startingHour, endingHour, false);
        workMonth.changeWorkDay(workDay);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertEquals(WORK_DAYS_CHANGED_EVENT, workMonthEvent.get().getClass());

        final WorkDaysChangedEvent workDaysChangedEvent = (WorkDaysChangedEvent) workMonthEvent.get();
        assertEquals(WORK_MONTH_ID, workDaysChangedEvent.aggregateId());
        assertTrue(workDaysChangedEvent.workDays().contains(workDay));
    }

    @Test
    @DisplayName("WorkMonth Not Should Change WorkDay When Haven't Changed")
    void shouldNotChangeWorkDayWhenHaveNotChanged() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID,YEAR_MONTH);
        final WorkDay workDay = new WorkDay(YEAR_MONTH.atDay(1), false);
        workMonth.changeWorkDay(workDay);

        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertNotEquals(WORK_DAYS_CHANGED_EVENT, workMonthEvent.get().getClass());
    }

    @Test
    @DisplayName("WorkMonth Should Throws 'WorkDayInvalidException' When WorkDay Not In Range")
    void shouldThrowsWorkDayInvalidExceptionWhenWorkDayNotInRange() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YEAR_MONTH);

        final LocalDate startingDate = YEAR_MONTH.atDay(1);
        final WorkDay workDayWithDayBefore = new WorkDay(startingDate.minusDays(1), false);
        assertThrows(WORK_DAY_INVALID_EXCEPTION, () -> workMonth.changeWorkDay(workDayWithDayBefore));

        final LocalDate endingDate = YEAR_MONTH.atEndOfMonth().plusDays(1);
        final WorkDay workDayWithDayAfter = new WorkDay(endingDate, false);
        assertThrows(WORK_DAY_INVALID_EXCEPTION, () -> workMonth.changeWorkDay(workDayWithDayAfter));
    }

    @Test
    @DisplayName("WorkMonth Should Calculate Zero Hours When Not Changing WorkDays")
    void shouldCalculateZeroHoursWhenNotChangingWorkDays() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YEAR_MONTH);
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
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YEAR_MONTH);

        final WorkHour first = new WorkHour(8, 30);
        final WorkHour second = new WorkHour(17, 0);
        final WorkDay workDay = new WorkDay(YEAR_MONTH.atDay(1), first, second, false);

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
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YEAR_MONTH);

        final WorkHour first = new WorkHour(8, 30);
        final WorkHour second = new WorkHour(17, 0);
        final WorkDay workDay = new WorkDay(YEAR_MONTH.atDay(1), first, second, true);

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

    @Test
    @DisplayName("WorkMonth Should Changed All WorkDays When All In Range And Data Changed")
    void shouldChangedAllWorkDaysWhenAllInRangeAndDataChanged() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YEAR_MONTH);
        final WorkHour differentWorkHour = new WorkHour(10, 10);
        final WorkDay first = new WorkDay(YEAR_MONTH.atDay(1), differentWorkHour, differentWorkHour, false);
        final WorkDay second = new WorkDay(YEAR_MONTH.atDay(2), differentWorkHour, differentWorkHour, false);

        workMonth.changeWorkDays(Set.of(first, second));
        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertEquals(WORK_DAYS_CHANGED_EVENT, workMonthEvent.get().getClass());

        final WorkDaysChangedEvent workDaysChangedEvent = (WorkDaysChangedEvent) workMonthEvent.get();
        assertEquals(workDaysChangedEvent.aggregateId(), WORK_MONTH_ID);
        assertEquals(2, workDaysChangedEvent.workDays().size());
        assertTrue(workDaysChangedEvent.workDays().contains(first));
        assertTrue(workDaysChangedEvent.workDays().contains(second));
    }

    @Test
    @DisplayName("WorkMonth Should Changed Only One WorkDay")
    void shouldChangeOnlyOneWorkDay() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YEAR_MONTH);
        final WorkHour differentWorkHour = new WorkHour(10, 10);
        final WorkDay first = new WorkDay(YEAR_MONTH.atDay(1), differentWorkHour, differentWorkHour, false);
        final WorkDay second = new WorkDay(YEAR_MONTH.atDay(2), WorkHour.zero(), WorkHour.zero(), false);

        workMonth.changeWorkDays(Set.of(first, second));
        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertEquals(WORK_DAYS_CHANGED_EVENT, workMonthEvent.get().getClass());

        final WorkDaysChangedEvent workDaysChangedEvent = (WorkDaysChangedEvent) workMonthEvent.get();
        assertEquals(workDaysChangedEvent.aggregateId(), WORK_MONTH_ID);
        assertEquals(1, workDaysChangedEvent.workDays().size());
        assertTrue(workDaysChangedEvent.workDays().contains(first));
        assertFalse(workDaysChangedEvent.workDays().contains(second));
    }

    @Test
    @DisplayName("WorkMonth Should Not Change WorkDays")
    void shouldNotChangeWorkDays() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YEAR_MONTH);
        final WorkDay first = new WorkDay(YEAR_MONTH.atDay(1), WorkHour.zero(), WorkHour.zero(), false);
        final WorkDay second = new WorkDay(YEAR_MONTH.atDay(2), WorkHour.zero(), WorkHour.zero(), false);

        workMonth.changeWorkDays(Set.of(first, second));
        final Optional<WorkMonthEvent> workMonthEvent = workMonth.findLatestEvent();
        assertTrue(workMonthEvent.isPresent());
        assertNotEquals(WORK_DAYS_CHANGED_EVENT, workMonthEvent.get().getClass());
    }

    @Test
    @DisplayName("WorkMonth Should Throw 'WorkDayInvalidException' When Not In Range")
    void shouldThrowWorkDayInvalidExceptionWhenNotInRange() {
        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YEAR_MONTH);
        final WorkDay first = new WorkDay(YEAR_MONTH.atDay(1), WorkHour.zero(), WorkHour.zero(), false);
        final WorkDay second = new WorkDay(YEAR_MONTH.atDay(2).minusDays(10), WorkHour.zero(), WorkHour.zero(), false);

        assertThrows(WORK_DAY_INVALID_EXCEPTION, () -> workMonth.changeWorkDays(Set.of(first, second)));
    }
}