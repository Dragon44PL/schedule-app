package com.github.schedule.workmonth;

import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.vo.UserId;
import com.github.schedule.workmonth.vo.WorkDay;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.YearMonth;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
class WorkMonthCreatedHandlerTest {

    /*
       Data
     */

    private final UUID WORK_MONTH_ID = UUID.randomUUID();
    private final UserId USER_ID = new UserId(UUID.randomUUID());

    @InjectMocks
    private WorkMonthCreatedHandler workMonthCreatedHandler;

    /*
        Mock
     */

    @Mock
    private WorkMonthEntityRepository workMonthEntityRepository;

    @Test
    @DisplayName("Should Save WorkMonthEntity From WorkMonthCreatedEvent Properly")
    void shouldSaveEntityFromWorkMonthCreatedEvent() {
        when(workMonthEntityRepository.saveAndFlush(any(WorkMonthEntity.class))).thenAnswer(i -> i.getArgument(0));

        final WorkMonth workMonth = WorkMonth.create(WORK_MONTH_ID, USER_ID, YearMonth.now());
        final WorkMonthCreatedEvent workMonthCreatedEvent = (WorkMonthCreatedEvent) workMonth.findLatestEvent().get();
        workMonthCreatedHandler.handle(workMonthCreatedEvent);

        final ArgumentCaptor<WorkMonthEntity> workMonthEntityCaptor = ArgumentCaptor.forClass(WorkMonthEntity.class);
        verify(workMonthEntityRepository, times(1)).saveAndFlush(workMonthEntityCaptor.capture());

        verifyResults(workMonthCreatedEvent, workMonthEntityCaptor.getValue());
    }

    private void verifyResults(WorkMonthCreatedEvent workMonthCreatedEvent, WorkMonthEntity workMonthEntity) {
        final Set<WorkDay> workDays = workMonthCreatedEvent.workDays();
        assertNotNull(workMonthEntity);
        assertEquals(workMonthCreatedEvent.aggregateId(), workMonthEntity.getId());
        assertEquals(workMonthCreatedEvent.userId().id(), workMonthEntity.getUserId());
        assertEquals(workMonthCreatedEvent.yearMonth(), YearMonth.of(workMonthEntity.getYearMonth().getYear(), workMonthEntity.getYearMonth().getMonth()));
        assertEquals(workMonthCreatedEvent.totalHours().hours(), workMonthEntity.getTotalHours().getHours());
        assertEquals(workMonthCreatedEvent.totalHours().minutes(), workMonthEntity.getTotalHours().getMinutes());
        workMonthEntity.getWorkDays().forEach(workDayEntity -> checkWorkDayEntity(workDays, workMonthEntity, workDayEntity));
    }

    private void checkWorkDayEntity(Set<WorkDay> workDays, WorkMonthEntity workMonthEntity, WorkDayEntity workDayEntity) {
        final Optional<WorkDay> found = workDays.stream().filter(workDay -> workDay.sameDate(workDayEntity.getDate())).findFirst();
        assertTrue(found.isPresent());
        checkWorkMonth(workMonthEntity, workDayEntity);
        checkWorkDay(found.get(), workDayEntity);
    }

    private void checkWorkMonth(WorkMonthEntity workMonthEntity, WorkDayEntity workDayEntity) {
        assertEquals(workMonthEntity, workDayEntity.getWorkMonth());
    }

    private void checkWorkDay(WorkDay workDay, WorkDayEntity workDayEntity) {
        assertEquals(workDay.isLeave(), workDayEntity.isLeave());
        assertEquals(workDay.date(), workDayEntity.getDate());

        assertEquals(workDay.startingHour().hours(), workDayEntity.getStartingHour().getHour());
        assertEquals(workDay.startingHour().minutes(), workDayEntity.getStartingHour().getMinute());

        assertEquals(workDay.endingHour().hours(), workDayEntity.getEndingHour().getHour());
        assertEquals(workDay.endingHour().minutes(), workDayEntity.getEndingHour().getMinute());
    }
}
