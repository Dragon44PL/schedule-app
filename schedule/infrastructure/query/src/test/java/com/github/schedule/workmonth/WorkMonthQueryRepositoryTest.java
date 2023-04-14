package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkHourQueryDto;
import com.github.schedule.workmonth.dto.WorkMonthQueryDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class WorkMonthQueryRepositoryTest {

    private WorkMonthQueryRepository workMonthQueryRepository;

    /*
        Data
     */

    final WorkHourQueryDto WORK_HOUR = new WorkHourQueryDto(LocalTime.now().getHour(), LocalTime.now().getMinute());
    final WorkMonthQueryDto WORK_MONTH = new WorkMonthQueryDto(UUID.randomUUID(), UUID.randomUUID(), YearMonth.now(), WORK_HOUR, new HashSet<>());

    @BeforeEach
    void set() {

        /*
            Creating WorkMonthEntityQueryRepository
         */

        this.workMonthQueryRepository = Mockito.mock(WorkMonthQueryRepository.class);
    }

    @Test
    @DisplayName("Should Find WorkMonth By Id")
    void shouldFindWorkMonthById() {
        when(workMonthQueryRepository.findById(any())).thenAnswer((arg) -> {
            final UUID id = arg.getArgument(0);
            return id.equals(WORK_MONTH.id()) ? Optional.of(WORK_MONTH) : Optional.empty();
        });

        final Optional<WorkMonthQueryDto> workMonthQueryDto = workMonthQueryRepository.findById(WORK_MONTH.id());
        assertTrue(workMonthQueryDto.isPresent());
    }

    @Test
    @DisplayName("Should Not Find WorkMonth By Id")
    void shouldNotFindWorkMonthById() {
        when(workMonthQueryRepository.findById(any())).thenAnswer((arg) -> {
            final UUID id = arg.getArgument(0);
            return id.equals(WORK_MONTH.id()) ? Optional.of(WORK_MONTH) : Optional.empty();
        });

        final Optional<WorkMonthQueryDto> workMonthQueryDto = workMonthQueryRepository.findById(UUID.randomUUID());
        assertFalse(workMonthQueryDto.isPresent());
    }

    @Test
    @DisplayName("Should Find WorkMonth By UserId")
    void shouldFindWorkMonthByUserId() {
        when(workMonthQueryRepository.findAllByUserId(any())).thenAnswer((arg) -> {
            final UUID id = arg.getArgument(0);
            return id.equals(WORK_MONTH.userId()) ? List.of(WORK_MONTH) : new ArrayList<>();
        });

        final List<WorkMonthQueryDto> workMonthQueryDto = workMonthQueryRepository.findAllByUserId(WORK_MONTH.userId());
        assertEquals(1, workMonthQueryDto.size());
    }

    @Test
    @DisplayName("Should Not Find WorkMonth By UserId")
    void shouldNotFindWorkMonthByUserId() {
        when(workMonthQueryRepository.findAllByUserId(any())).thenAnswer((arg) -> {
            final UUID id = arg.getArgument(0);
            return id.equals(WORK_MONTH.userId()) ? List.of(WORK_MONTH) : new ArrayList<>();
        });

        final List<WorkMonthQueryDto> workMonthQueryDto = workMonthQueryRepository.findAllByUserId(UUID.randomUUID());
        assertEquals(0, workMonthQueryDto.size());
    }
}
