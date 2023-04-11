package com.github.schedule.workmonth;

import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.vo.WorkHour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
class TotalHoursCalculatedHandlerTest {

    /*
        Data
     */

    private final WorkMonthEntity WORK_MONTH_ENTITY = WorkMonthEntity.builder().id(UUID.randomUUID()).yearMonth(new YearMonthEntity(YearMonth.now().getYear(), YearMonth.now().getMonthValue())).workDays(new HashSet<>())
                                                                    .totalHours(WorkHourEntity.builder().hours(10).minutes(10).build()).build();

    @InjectMocks
    private TotalHoursCalculatedHandler totalHoursCalculatedHandler;

    /*
        Mocks
     */

    @Mock
    private WorkMonthEntityRepository workMonthEntityRepository;

    @Test
    @DisplayName("Should Return WorkMonthEntity When Exists And Modify With TotalHoursCalculatedEvent")
    void shouldReturnWorkMonthEntityWhenExists() {
        when(workMonthEntityRepository.findById(any(UUID.class))).thenReturn(Optional.of(WORK_MONTH_ENTITY));
        when(workMonthEntityRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        final TotalHoursCalculatedEvent event = new TotalHoursCalculatedEvent(WORK_MONTH_ENTITY.getId(), WorkHour.zero());
        totalHoursCalculatedHandler.handle(event);

        ArgumentCaptor<WorkMonthEntity> workMonthEntityCaptor = ArgumentCaptor.forClass(WorkMonthEntity.class);
        verify(workMonthEntityRepository, times(1)).findById(any());
        verify(workMonthEntityRepository, times(1)).save(workMonthEntityCaptor.capture());

        final WorkMonthEntity captured = workMonthEntityCaptor.getValue();
        assertNotNull(captured);
        assertEquals(captured.getId(), WORK_MONTH_ENTITY.getId());
        assertEquals(captured.getYearMonth(), WORK_MONTH_ENTITY.getYearMonth());
        assertEquals(captured.getWorkDays(), WORK_MONTH_ENTITY.getWorkDays());
        assertEquals(captured.getTotalHours().getHours(), event.totalWorkHours().hours());
        assertEquals(captured.getTotalHours().getMinutes(), event.totalWorkHours().minutes());
    }

    @Test
    @DisplayName("Should Not Return WorkMonthEntity When Not Exists")
    void shouldNotReturnWorkMonthEntityWhenNotExists() {
        when(workMonthEntityRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        final TotalHoursCalculatedEvent event = new TotalHoursCalculatedEvent(WORK_MONTH_ENTITY.getId(), WorkHour.zero());
        totalHoursCalculatedHandler.handle(event);

        verify(workMonthEntityRepository, times(1)).findById(any());
        verify(workMonthEntityRepository, times(0)).save(any());

    }
}
