package com.github.schedule.workmonth;

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
public class WorkMonthQueryTest {

    private WorkMonthQuery workMonthQuery;

    private WorkMonthEntityQueryRepository workMonthEntityQueryRepository;

    /*
        Data
     */

    final WorkHourEntity WORK_HOUR_ENTITY = WorkHourEntity.builder().hours(LocalTime.now().getHour()).minutes(LocalTime.now().getMinute()).build();
    final WorkMonthEntity WORK_MONTH_ENTITY = WorkMonthEntity.builder().id(UUID.randomUUID()).userId(UUID.randomUUID()).date(YearMonth.now()).totalHours(WORK_HOUR_ENTITY).workDays(new HashSet<>()).build();

    @BeforeEach
    void set() {

        /*
            Creating WorkMonthEntityQueryRepository
         */

        this.workMonthEntityQueryRepository = Mockito.mock(WorkMonthEntityQueryRepository.class);

        /*
            Creating WorkMonthQueryRepository
         */

        this.workMonthQuery = new WorkMonthQuery(workMonthEntityQueryRepository);
    }

    @Test
    @DisplayName("Should Find All WorkMonths")
    void shouldFindAllWorkMonths() {
        when(workMonthEntityQueryRepository.findAll()).thenReturn(List.of(WORK_MONTH_ENTITY));

        final List<WorkMonthQueryDto> workMonthQueryDto = workMonthQuery.findAll();
        assertEquals(1, workMonthQueryDto.size());
    }

    @Test
    @DisplayName("Should Not Find WorkMonths")
    void shouldNotFindWorkMonths() {
        when(workMonthEntityQueryRepository.findAll()).thenReturn(new ArrayList<>());

        final List<WorkMonthQueryDto> workMonthQueryDto = workMonthQuery.findAll();
        assertEquals(0, workMonthQueryDto.size());
    }

    @Test
    @DisplayName("Should Find WorkMonth By Id")
    void shouldFindWorkMonthById() {
        when(workMonthEntityQueryRepository.findById(any())).thenAnswer((arg) -> {
            final UUID id = arg.getArgument(0);
            return id.equals(WORK_MONTH_ENTITY.getId()) ? Optional.of(WORK_MONTH_ENTITY) : Optional.empty();
        });

        final Optional<WorkMonthQueryDto> workMonthQueryDto = workMonthQuery.findById(WORK_MONTH_ENTITY.getId());
        assertTrue(workMonthQueryDto.isPresent());
    }

    @Test
    @DisplayName("Should Not Find WorkMonth By Id")
    void shouldNotFindWorkMonthById() {
        when(workMonthEntityQueryRepository.findById(any())).thenAnswer((arg) -> {
            final UUID id = arg.getArgument(0);
            return id.equals(WORK_MONTH_ENTITY.getId()) ? Optional.of(WORK_MONTH_ENTITY) : Optional.empty();
        });

        final Optional<WorkMonthQueryDto> workMonthQueryDto = workMonthQuery.findById(UUID.randomUUID());
        assertFalse(workMonthQueryDto.isPresent());
    }

    @Test
    @DisplayName("Should Find WorkMonth By UserId")
    void shouldFindWorkMonthByUserId() {
        when(workMonthEntityQueryRepository.findAllByUserId(any())).thenAnswer((arg) -> {
            final UUID id = arg.getArgument(0);
            return id.equals(WORK_MONTH_ENTITY.getUserId()) ? List.of(WORK_MONTH_ENTITY) : new ArrayList<>();
        });

        final List<WorkMonthQueryDto> workMonthQueryDto = workMonthQuery.findAllByUserId(WORK_MONTH_ENTITY.getUserId());
        assertEquals(1, workMonthQueryDto.size());
    }

    @Test
    @DisplayName("Should Not Find WorkMonth By UserId")
    void shouldNotFindWorkMonthByUserId() {
        when(workMonthEntityQueryRepository.findAllByUserId(any())).thenAnswer((arg) -> {
            final UUID id = arg.getArgument(0);
            return id.equals(WORK_MONTH_ENTITY.getUserId()) ? List.of(WORK_MONTH_ENTITY) : new ArrayList<>();
        });

        final List<WorkMonthQueryDto> workMonthQueryDto = workMonthQuery.findAllByUserId(UUID.randomUUID());
        assertEquals(0, workMonthQueryDto.size());
    }

    @Test
    @DisplayName("Should Find All By Date")
    void shouldFindAllWorkMonthsByDate() {
        when(workMonthEntityQueryRepository.findAllByDate(any())).thenAnswer((arg) -> {
            final YearMonth yearMonth = arg.getArgument(0);
            return yearMonth.equals(WORK_MONTH_ENTITY.getDate()) ? List.of(WORK_MONTH_ENTITY) : new ArrayList<>();
        });

        final List<WorkMonthQueryDto> workMonthQueryDto = workMonthQuery.findAllByDate(WORK_MONTH_ENTITY.getDate());
        assertEquals(1, workMonthQueryDto.size());
    }

    @Test
    @DisplayName("Should Not Find By Date")
    void shouldNotFindWorkMonthByDate() {
        when(workMonthEntityQueryRepository.findAllByDate(any())).thenAnswer((arg) -> {
            final YearMonth yearMonth = arg.getArgument(0);
            return yearMonth.equals(WORK_MONTH_ENTITY.getDate()) ? List.of(WORK_MONTH_ENTITY) : new ArrayList<>();
        });

        final List<WorkMonthQueryDto> workMonthQueryDto = workMonthQuery.findAllByDate(WORK_MONTH_ENTITY.getDate().plusMonths(1));
        assertEquals(0, workMonthQueryDto.size());
    }
}
