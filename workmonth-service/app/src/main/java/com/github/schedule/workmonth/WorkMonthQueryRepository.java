package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkMonthQueryDto;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkMonthQueryRepository {
    List<WorkMonthQueryDto> findAll();
    List<WorkMonthQueryDto> findAllByDate(YearMonth yearMonth);
    Optional<WorkMonthQueryDto> findById(UUID id);
    List<WorkMonthQueryDto> findAllByUserId(UUID id);
}
