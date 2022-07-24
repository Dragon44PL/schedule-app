package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkMonthQueryDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
class WorkMonthQuery {

    private final WorkMonthEntityQueryRepository workMonthEntityQueryRepository;

    WorkMonthQuery(WorkMonthEntityQueryRepository workMonthEntityQueryRepository) {
        this.workMonthEntityQueryRepository = workMonthEntityQueryRepository;
    }

    List<WorkMonthQueryDto> findAll() {
        final List<WorkMonthEntity> workMonths = workMonthEntityQueryRepository.findAll();
        return workMonths.stream().map(WorkMonthDtoConverter::workMonthDto).collect(Collectors.toList());
    }

    List<WorkMonthQueryDto> findAllByDate(YearMonth yearMonth) {
        final List<WorkMonthEntity> workMonthEntity = workMonthEntityQueryRepository.findAllByDate(yearMonth);
        return workMonthEntity.stream().map(WorkMonthDtoConverter::workMonthDto).collect(Collectors.toList());
    }

    Optional<WorkMonthQueryDto> findById(UUID id) {
        final Optional<WorkMonthEntity> workMonthEntity = workMonthEntityQueryRepository.findById(id);
        return workMonthEntity.map(WorkMonthDtoConverter::workMonthDto);
    }

    public List<WorkMonthQueryDto> findAllByUserId(UUID id) {
        final List<WorkMonthEntity> workMonthEntity = workMonthEntityQueryRepository.findAllByUserId(id);
        return workMonthEntity.stream().map(WorkMonthDtoConverter::workMonthDto).collect(Collectors.toList());
    }

}
