package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkMonthQueryDto;
import com.github.schedule.workmonth.dto.WorkMonthResponseDto;
import com.github.schedule.workmonth.dto.converter.WorkMonthResponseConverter;
import com.github.schedule.workmonth.validation.constraint.uuid;
import com.sun.istack.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
class WorkMonthQueryEndpoint {

    private final WorkMonthQueryRepository workMonthEntityQueryRepository;
    private final WorkMonthResponseConverter workMonthResponseConverter;

    WorkMonthQueryEndpoint(WorkMonthQueryRepository workMonthEntityQueryRepository, WorkMonthResponseConverter workMonthResponseConverter) {
        this.workMonthEntityQueryRepository = workMonthEntityQueryRepository;
        this.workMonthResponseConverter = workMonthResponseConverter;
    }

    @GetMapping(value = "/api/workmonth", produces = MediaType.APPLICATION_JSON_VALUE)
    List<WorkMonthResponseDto> findAll(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        final List<WorkMonthQueryDto> query = (year != null && month != null)
                ? workMonthEntityQueryRepository.findAllByDate(YearMonth.of(year, month))
                : workMonthEntityQueryRepository.findAll();
        return query.stream().map(workMonthResponseConverter::convertWorkMonthResponseDto).collect(Collectors.toList());
    }

    @GetMapping("/api/workmonth/{id}")
    ResponseEntity<?> findById(@Valid @PathVariable @NotBlank @uuid UUID id) {
        final Optional<WorkMonthQueryDto> workMonthDto = workMonthEntityQueryRepository.findById(id);
        return workMonthDto.isPresent()
                ? ResponseEntity.ok(workMonthResponseConverter.convertWorkMonthResponseDto(workMonthDto.get()))
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/api/workmonth/{id}/total")
    ResponseEntity<?> findTotalHoursById(@Valid @PathVariable @NotNull @uuid UUID id) {
        final Optional<WorkMonthQueryDto> workMonthDto = workMonthEntityQueryRepository.findById(id);
        return workMonthDto.isPresent()
                ? ResponseEntity.ok(workMonthResponseConverter.convertWorkMonthResponseDto(workMonthDto.get()))
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/api/workmonth/user/{id}")
    List<WorkMonthResponseDto> findByUser(@Valid @PathVariable @NotNull @uuid UUID id) {
        final List<WorkMonthQueryDto> query = workMonthEntityQueryRepository.findAllByUserId(id);
        return query.stream().map(workMonthResponseConverter::convertWorkMonthResponseDto).collect(Collectors.toList());
    }
}