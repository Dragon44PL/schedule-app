package com.github.schedule.workmonth;

import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.event.WorkDaysChangedEvent;
import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.vo.UserId;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
class PersistenceWorkMonthHandlerTest {

    private static PersistenceWorkMonthHandler persistenceWorkMonthHandler;

    /*
        Mocks
     */

    private static TotalHoursCalculatedHandler totalHoursCalculatedHandler;

    private static WorkMonthCreatedHandler workMonthCreatedHandler;

    private static WorkDaysChangedHandler workDaysChangedHandler;

    @BeforeAll
    static void set() {
        // Creating TotalHoursCalculatedHandler
        totalHoursCalculatedHandler = Mockito.mock(TotalHoursCalculatedHandler.class);
        doNothing().when(totalHoursCalculatedHandler).handle(any(TotalHoursCalculatedEvent.class));

        // Creating WorkMonthCreatedHandler
        workMonthCreatedHandler = Mockito.mock(WorkMonthCreatedHandler.class);
        doNothing().when(workMonthCreatedHandler).handle(any(WorkMonthCreatedEvent.class));

        // Creating WorkDayUpdatedHandler
        workDaysChangedHandler = Mockito.mock(WorkDaysChangedHandler.class);
        doNothing().when(workDaysChangedHandler).handle(any(WorkDaysChangedEvent.class));

        persistenceWorkMonthHandler = new PersistenceWorkMonthHandler(workMonthCreatedHandler, workDaysChangedHandler, totalHoursCalculatedHandler);
    }

    @Test
    @DisplayName("Should Execute 'handle' of TotalHoursCalculatedHandler When TotalHoursCalculatedEvent Passed")
    void shouldExecuteTotalHoursCalculatedHandler() {
        final var totalHoursCalculatedEvent = new TotalHoursCalculatedEvent(UUID.randomUUID(), WorkHour.zero());
        persistenceWorkMonthHandler.handle(totalHoursCalculatedEvent);

        verify(totalHoursCalculatedHandler, times(1)).handle(any(TotalHoursCalculatedEvent.class));
    }

    @Test
    @DisplayName("Should Execute 'handle' of WorkMonthCreatedHandler When WorkMonthCreatedEvent Passed")
    void shouldExecuteWorkMonthCreatedHandler() {
        final var workMonthCreatedEvent = new WorkMonthCreatedEvent(UUID.randomUUID(), new UserId(UUID.randomUUID()), YearMonth.now(), WorkHour.zero(), new HashSet<>());
        persistenceWorkMonthHandler.handle(workMonthCreatedEvent);

        verify(workMonthCreatedHandler, times(1)).handle(any(WorkMonthCreatedEvent.class));
    }

    @Test
    @DisplayName("Should Execute 'handle' of WorkMonthUpdatedHandler When WorkDayUpdatedEvent Passed")
    void shouldExecuteWorkDayUpdatedHandler() {
        final var workDayUpdatedEvent = new WorkDaysChangedEvent(UUID.randomUUID(), Set.of(new WorkDay(LocalDate.now(), false)));
        persistenceWorkMonthHandler.handle(workDayUpdatedEvent);

        verify(workDaysChangedHandler, times(1)).handle(any(WorkDaysChangedEvent.class));
    }
}
