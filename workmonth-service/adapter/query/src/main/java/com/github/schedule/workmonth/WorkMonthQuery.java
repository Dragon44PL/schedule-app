package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkMonthQueryDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class WorkMonthQuery {

    private final WorkMonthEntityQueryRepository workMonthEntityQueryRepository;
    private final WorkMonthDtoConverter workMonthDtoConverter;

    public WorkMonthQuery(WorkMonthEntityQueryRepository workMonthEntityQueryRepository, WorkMonthDtoConverter workMonthDtoConverter) {
        this.workMonthEntityQueryRepository = workMonthEntityQueryRepository;
        this.workMonthDtoConverter = workMonthDtoConverter;
    }

    public List<WorkMonthQueryDto> findAll() {
        final List<WorkMonthEntity> workMonths = workMonthEntityQueryRepository.findAll();
        return workMonths.stream().map(workMonthDtoConverter::workMonthDto).collect(Collectors.toList());
    }

    public List<WorkMonthQueryDto> findAllByDate(YearMonth yearMonth) {
        final List<WorkMonthEntity> workMonthEntity = workMonthEntityQueryRepository.findAllByDate(yearMonth);
        return workMonthEntity.stream().map(workMonthDtoConverter::workMonthDto).collect(Collectors.toList());
    }

    public Optional<WorkMonthQueryDto> findById(UUID id) {
        final Optional<WorkMonthEntity> workMonthEntity = workMonthEntityQueryRepository.findById(id);
        return workMonthEntity.map(workMonthDtoConverter::workMonthDto);
    }

    public List<WorkMonthQueryDto> findAllByUserId(UUID id) {
        final List<WorkMonthEntity> workMonthEntity = workMonthEntityQueryRepository.findAllByUserId(id);
        return workMonthEntity.stream().map(workMonthDtoConverter::workMonthDto).collect(Collectors.toList());
    }

}
