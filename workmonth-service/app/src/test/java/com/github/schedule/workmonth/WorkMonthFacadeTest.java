package com.github.schedule.workmonth;

import com.github.schedule.workmonth.command.WorkDaysChangeCommand;
import com.github.schedule.workmonth.command.WorkMonthCreateCommand;
import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.event.WorkDaysChangedEvent;
import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.exception.WorkDayOutOfRangeException;
import com.github.schedule.workmonth.exception.WorkMonthExistsException;
import com.github.schedule.workmonth.vo.UserId;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkMonthFacadeTest {

    @InjectMocks
    private WorkMonthFacade workMonthFacade;

    /*
        Mocks
     */

    @Mock
    private WorkMonthRepository workMonthRepository;

    /*
        WorkMonth Events
     */

    private final Class<WorkMonthCreatedEvent> WORK_MONTH_CREATED_EVENT = WorkMonthCreatedEvent.class;
    private final Class<WorkDaysChangedEvent> WORK_DAYS_CHANGED_EVENT = WorkDaysChangedEvent.class;
    private final Class<TotalHoursCalculatedEvent> TOTAL_HOURS_CALCULATED_EVENT = TotalHoursCalculatedEvent.class;

    /*
        WorkMonth Exceptions
     */

    private final Class<WorkDayOutOfRangeException> WORK_DAY_OUT_OF_RANGE_EXCEPTION = WorkDayOutOfRangeException.class;
    private final Class<WorkMonthExistsException> WORK_MONTH_EXISTS_EXCEPTION = WorkMonthExistsException.class;

    @Test
    @DisplayName("Should Create WorkMonth Properly And Return WorkMonthCreatedEvent")
    void shouldCreateWorkMonthProperlyAndReturnEvent() {
        when(workMonthRepository.findByUser(any(), any())).thenReturn(Optional.empty());
        doNothing().when(workMonthRepository).save(any());

        /*
            Perform domain operation and check returned event correctness
         */

        final WorkMonthCreateCommand command = new WorkMonthCreateCommand(YearMonth.now(), new UserId(UUID.randomUUID()));
        final List<WorkMonthEvent> workMonthEvents = workMonthFacade.createWorkMonth(command);
        assertFalse(workMonthEvents.isEmpty());
        assertEquals(WORK_MONTH_CREATED_EVENT, workMonthEvents.get(0).getClass());

        /*
            Check if all repository operations were performed correct amount of times and take valid data
         */

        final ArgumentCaptor<WorkMonth> workMonthArgumentCaptor = ArgumentCaptor.forClass(WorkMonth.class);
        verify(workMonthRepository, times(1)).findByUser(any(), any());
        verify(workMonthRepository, times(1)).save(workMonthArgumentCaptor.capture());

        final WorkMonth workMonth = workMonthArgumentCaptor.getValue();
        assertNotNull(workMonth);
    }

    @Test
    @DisplayName("Should Throw 'WorkMonthExistsException' When WorkMonth Already Exists")
    void shouldThrowWorkMonthExistsExceptionWhenWorkMonthAlreadyExists() {
        final WorkMonth example = WorkMonth.restore(UUID.randomUUID(), new UserId(UUID.randomUUID()), YearMonth.now(), new HashSet<>());
        when(workMonthRepository.findByUser(any(), any())).thenReturn(Optional.of(example));

        /*
            Perform domain operation and check if an exception has been thrown
         */

        final WorkMonthCreateCommand command = new WorkMonthCreateCommand(YearMonth.now(), new UserId(UUID.randomUUID()));
        assertThrows(WORK_MONTH_EXISTS_EXCEPTION, () -> workMonthFacade.createWorkMonth(command));

        /*
            Check if all repository operations were performed correct amount of times
         */

        verify(workMonthRepository, times(1)).findByUser(any(), any());
        verify(workMonthRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should Update WorkDays Properly When WorkMonth Is Found")
    void shouldUpdateWorkDaysOfWorkMonthProperly() {
        final WorkMonthCreatedEvent event = (WorkMonthCreatedEvent) WorkMonth.create(UUID.randomUUID(), new UserId(UUID.randomUUID()), YearMonth.now()).events().get(0);
        final WorkMonth workMonth = WorkMonth.restore(event.aggregateId(), event.userId(), event.yearMonth(), event.workDays());

        when(workMonthRepository.findById(any(UUID.class))).thenReturn(Optional.of(workMonth));
        doNothing().when(workMonthRepository).save(any());

        /*
            Perform domain operation and check returned event correctness
         */

        final WorkDay workDay = new WorkDay(LocalDate.now(), new WorkHour(10, 10), new WorkHour(10, 10), false);
        final WorkDaysChangeCommand command = new WorkDaysChangeCommand(UUID.randomUUID(), Set.of(workDay));
        final List<WorkMonthEvent> workMonthEvents = workMonthFacade.updateWorkDays(command);
        assertFalse(workMonthEvents.isEmpty());
        assertEquals(2, workMonthEvents.size());
        assertEquals(WORK_DAYS_CHANGED_EVENT, workMonthEvents.get(0).getClass());
        assertEquals(TOTAL_HOURS_CALCULATED_EVENT, workMonthEvents.get(1).getClass());

        /*
            Check if all repository operations were performed correct amount of times and take valid data
         */

        final ArgumentCaptor<WorkMonth> workMonthArgumentCaptor = ArgumentCaptor.forClass(WorkMonth.class);
        verify(workMonthRepository, times(1)).findById(any(UUID.class));
        verify(workMonthRepository, times(1)).save(workMonthArgumentCaptor.capture());

        final WorkMonth capturedWorkMonth = workMonthArgumentCaptor.getValue();
        assertNotNull(capturedWorkMonth);

        /*
            Check if WorkMonth properly changed WorkDays and both (pre and post) operation data are the same
         */

        final WorkDaysChangedEvent workDaysChangedEvent = (WorkDaysChangedEvent) workMonthEvents.get(0);
        assertEquals(command.workDays().size(), workDaysChangedEvent.workDays().size());
        assertNotEquals(0, workDaysChangedEvent.workDays().size());

        final WorkDay changedWorkDay = workDaysChangedEvent.workDays().iterator().next();
        assertEquals(workDay.startingHour(), changedWorkDay.startingHour());
        assertEquals(workDay.endingHour(), changedWorkDay.endingHour());
        assertEquals(workDay.isLeave(), changedWorkDay.isLeave());
        assertEquals(workDay.date(), changedWorkDay.date());

    }

    @Test
    @DisplayName("Should Not Update WorkDays When WorkMonth Is Not Found")
    void shouldNotUpdateWorkDaysWhenWorkMonthNotFound() {
        when(workMonthRepository.findById(any())).thenReturn(Optional.empty());

        /*
            Perform domain operation and check returned event correctness
         */

        final WorkDay workDay = new WorkDay(LocalDate.now(), new WorkHour(10, 10), new WorkHour(10, 10), false);
        final WorkDaysChangeCommand command = new WorkDaysChangeCommand(UUID.randomUUID(), Set.of(workDay));
        final List<WorkMonthEvent> workMonthEvents = workMonthFacade.updateWorkDays(command);
        assertTrue(workMonthEvents.isEmpty());

        /*
            Check if all repository operations were performed correct amount of times and take valid data
         */

        verify(workMonthRepository, times(1)).findById(any(UUID.class));
        verify(workMonthRepository, times(0)).save(any(WorkMonth.class));
    }

    @Test
    @DisplayName("Should Not Update WorkDays When Have Not Changed")
    void shouldNotUpdateWorkDaysWhenWorkDayHasNotChange() {
        final WorkMonthCreatedEvent event = (WorkMonthCreatedEvent) WorkMonth.create(UUID.randomUUID(), new UserId(UUID.randomUUID()), YearMonth.now()).events().get(0);
        final WorkMonth workMonth = WorkMonth.restore(event.aggregateId(), event.userId(), event.yearMonth(), event.workDays());

        when(workMonthRepository.findById(any(UUID.class))).thenReturn(Optional.of(workMonth));

        /*
            Perform domain operation and check returned event correctness
         */

        final WorkDay first = new WorkDay(event.yearMonth().atDay(1), false);
        final WorkDay second = new WorkDay(event.yearMonth().atDay(2), false);
        final WorkDaysChangeCommand command = new WorkDaysChangeCommand(UUID.randomUUID(), Set.of(first, second));
        final List<WorkMonthEvent> workMonthEvents = workMonthFacade.updateWorkDays(command);
        assertFalse(workMonthEvents.isEmpty());
        assertEquals(WORK_DAYS_CHANGED_EVENT, workMonthEvents.get(0).getClass());

        final WorkDaysChangedEvent workDaysChangedEvent = (WorkDaysChangedEvent) workMonthEvents.get(0);
        assertTrue(workDaysChangedEvent.workDays().containsAll(List.of(first, second)));

        /*
            Check if all repository operations were performed correct amount of times and take valid data
         */

        verify(workMonthRepository, times(1)).findById(any(UUID.class));
        verify(workMonthRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should Throw 'WorkDayInvalidException' When WorkDay Out Of Range")
    void shouldThrowExceptionWhenWorkDayOutOfRange() {
        final WorkMonthCreatedEvent event = (WorkMonthCreatedEvent) WorkMonth.create(UUID.randomUUID(), new UserId(UUID.randomUUID()), YearMonth.now()).events().get(0);
        final WorkMonth workMonth = WorkMonth.restore(event.aggregateId(), event.userId(), event.yearMonth(), event.workDays());

        when(workMonthRepository.findById(any(UUID.class))).thenReturn(Optional.of(workMonth));

        final WorkDay first = new WorkDay(event.yearMonth().atDay(1), false);
        final WorkDay second = new WorkDay(event.yearMonth().atDay(2).minusDays(10), false);
        final WorkDaysChangeCommand command = new WorkDaysChangeCommand(UUID.randomUUID(), Set.of(first, second));
        assertThrows(WORK_DAY_OUT_OF_RANGE_EXCEPTION, () -> workMonthFacade.updateWorkDays(command));
    }
}