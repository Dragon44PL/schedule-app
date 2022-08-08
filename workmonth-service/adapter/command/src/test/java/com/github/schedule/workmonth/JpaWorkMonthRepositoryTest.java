package com.github.schedule.workmonth;

import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.vo.UserId;
import com.github.schedule.workmonth.vo.WorkHour;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
class JpaWorkMonthRepositoryTest {

    /*
        Data
     */

    private static final UUID EXISTS_ID = UUID.randomUUID();
    private static final UUID NOT_EXISTS_ID = UUID.randomUUID();

    private static final int CALL_HANDLE_AMOUNT = 3;

    /*
        Mocks
     */

    private static WorkMonthEntityRepository workMonthEntityRepository;

    private static PersistenceWorkMonthHandler workMonthHandler;

    private static JpaWorkMonthRepository workMonthRepository;

    @BeforeAll
    static void set() {

        // Creating WorkMonthEntityRepository
        workMonthEntityRepository = Mockito.mock(WorkMonthEntityRepository.class);
        final WorkMonthEntity workMonthEntity = WorkMonthEntity.builder()
                .id(EXISTS_ID).date(YearMonth.now()).workDays(new HashSet<>())
                .totalHours(WorkHourEntity.builder().hours(0).minutes(0).build()).build();

        when(workMonthEntityRepository.findById(EXISTS_ID)).thenReturn(Optional.of(workMonthEntity));
        when(workMonthEntityRepository.findById(NOT_EXISTS_ID)).thenReturn(Optional.empty());

        // Creating WorkMonthHandler
        workMonthHandler = Mockito.mock(PersistenceWorkMonthHandler.class);
        doNothing().when(workMonthHandler).handle(any());

        workMonthRepository = new JpaWorkMonthRepository(workMonthEntityRepository, workMonthHandler);
    }

    @Test
    @DisplayName("Should Return WorkMonth When Exists With Specified Id")
    void shouldReturnWorkMonthWhenExistsWithSpecifiedId() {
        Optional<WorkMonth> workMonth = workMonthRepository.findById(EXISTS_ID);
        assertTrue(workMonth.isPresent());
    }

    @Test
    @DisplayName("Should Not Return WorkMonth When Not Exists With Specified Id")
    void shouldReturnWorkMonthWhenNotExistsWithSpecifiedId() {
        Optional<WorkMonth> workMonth = workMonthRepository.findById(NOT_EXISTS_ID);
        assertFalse(workMonth.isPresent());
    }

    @Test
    @DisplayName("Should Call WorkMonthHandler 'handle' method correct amount of times")
    void shouldCallWorkMonthHandlerCorrectAmountOfTimes() {
        final List<WorkMonthEvent> workMonthEvents = IntStream.range(0, CALL_HANDLE_AMOUNT)
                                                            .mapToObj((i) -> new TotalHoursCalculatedEvent(EXISTS_ID, WorkHour.zero()))
                                                            .collect(Collectors.toList());

        final WorkMonth workMonth = WorkMonth.restore(EXISTS_ID, new UserId(EXISTS_ID), YearMonth.now(), new HashSet<>(), workMonthEvents);

        workMonthRepository.save(workMonth);
        verify(workMonthHandler, times(CALL_HANDLE_AMOUNT)).handle(any());
    }

}
