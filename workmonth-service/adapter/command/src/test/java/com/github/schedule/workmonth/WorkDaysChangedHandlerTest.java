package com.github.schedule.workmonth;

import com.github.schedule.workmonth.event.WorkDaysChangedEvent;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
class WorkDaysChangedHandlerTest {

    /*
        Data
     */


    @InjectMocks
    private WorkDaysChangedHandler workDaysChangedHandler;

    /*
        Mock
     */

    @Mock
    private WorkMonthEntityRepository workMonthEntityRepository;

    @Test
    @DisplayName("Should Not Update WorkDays When WorkMonth Not Found")
    void shouldNotUpdateWorkDaysWhenWorkMonthNotFound() {
        when(workMonthEntityRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        final WorkDay workDay = new WorkDay(LocalDate.now(), WorkHour.zero(), WorkHour.zero(), false);
        final WorkDaysChangedEvent workDaysChangedEvent = new WorkDaysChangedEvent(UUID.randomUUID(), Set.of(workDay));

        workDaysChangedHandler.handle(workDaysChangedEvent);

        verify(workMonthEntityRepository, times(1)).findById(any(UUID.class));
        verify(workMonthEntityRepository, times(0)).save(any(WorkMonthEntity.class));
    }

    @Test
    @DisplayName("Should Update All WorkDays From 'WorkDaysChangedEvent'")
    void shouldUpdateAllWorkDaysFromEvent() {
        final LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.now());
        final WorkHourEntity workHourEntity = WorkHourEntity.builder().hours(0).minutes(0).build();

        final WorkDayEntity firstWorkDayEntity = WorkDayEntity.builder().date(LocalDate.now()).startingHour(localDateTime).endingHour(localDateTime).build();
        final WorkDayEntity secondWorkDayEntity = WorkDayEntity.builder().date(LocalDate.now().plusDays(1)).startingHour(localDateTime).endingHour(localDateTime).build();

        final WorkMonthEntity workMonthEntity = WorkMonthEntity.builder().id(UUID.randomUUID()).date(YearMonth.now()).workDays(Set.of(firstWorkDayEntity, secondWorkDayEntity))
                                                            .totalHours(workHourEntity).build();

        when(workMonthEntityRepository.findById(any())).thenReturn(Optional.of(workMonthEntity));
        when(workMonthEntityRepository.save(any(WorkMonthEntity.class))).thenAnswer(i -> i.getArgument(0));

        final WorkDay firstWorkDay = new WorkDay(firstWorkDayEntity.getDate(), new WorkHour(10, 10), new WorkHour(10, 10), false);
        final WorkDay secondWorkDay = new WorkDay(secondWorkDayEntity.getDate(), new WorkHour(15, 15), new WorkHour(15, 15), false);

        final WorkDaysChangedEvent workDaysChangedEvent = new WorkDaysChangedEvent(UUID.randomUUID(), Set.of(firstWorkDay, secondWorkDay));
        workDaysChangedHandler.handle(workDaysChangedEvent);

        final ArgumentCaptor<WorkMonthEntity> workMonthEntityArgumentCaptor = ArgumentCaptor.forClass(WorkMonthEntity.class);
        verify(workMonthEntityRepository, times(1)).findById(any());
        verify(workMonthEntityRepository, times(1)).save(workMonthEntityArgumentCaptor.capture());

        final WorkMonthEntity savedEntity = workMonthEntityArgumentCaptor.getValue();
        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getWorkDays());
        assertNotEquals(0, savedEntity.getWorkDays().size());
        savedEntity.getWorkDays().forEach(savedWorkDay -> {
            final Optional<WorkDay> found = workDaysChangedEvent.workDays().stream().filter(date -> date.sameDate(savedWorkDay.getDate())).findAny();
            assertTrue(found.isPresent());
            compareWorkDaysWithEntities(found.get(), savedWorkDay);
        });
    }

    private void compareWorkDaysWithEntities(WorkDay workDay, WorkDayEntity workDayEntity) {
        assertEquals(workDay.startingHour().hours(), workDayEntity.getStartingHour().getHour());
        assertEquals(workDay.startingHour().minutes(), workDayEntity.getStartingHour().getMinute());

        assertEquals(workDay.endingHour().hours(), workDayEntity.getEndingHour().getHour());
        assertEquals(workDay.endingHour().minutes(), workDayEntity.getEndingHour().getMinute());

        assertEquals(workDay.isLeave(), workDayEntity.isLeave());
        assertEquals(workDay.date(), workDayEntity.getDate());
    }
}
