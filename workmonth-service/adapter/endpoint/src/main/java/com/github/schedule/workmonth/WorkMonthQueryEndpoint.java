package com.github.schedule.workmonth;

import com.github.schedule.workmonth.converter.WorkMonthResponseConverter;
import com.github.schedule.workmonth.dto.WorkMonthQueryDto;
import com.github.schedule.workmonth.dto.response.WorkMonthResponseDto;
import com.github.schedule.workmonth.validation.constraint.UniqueIdentifier;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Workmonth query endpoint")
class WorkMonthQueryEndpoint {

    private final WorkMonthQuery workMonthEntityQueryRepository;

    @GetMapping(value = "/api/workmonth", produces = MediaType.APPLICATION_JSON_VALUE)
    List<WorkMonthResponseDto> findAll(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        var query = (year != null && month != null)
                ? workMonthEntityQueryRepository.findAllByDate(YearMonth.of(year, month))
                : workMonthEntityQueryRepository.findAll();

        return query.stream()
                .map(WorkMonthResponseConverter::convertWorkMonthResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/api/workmonth/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> findById(@Valid @PathVariable @NotBlank @UniqueIdentifier String id) {
        final Optional<WorkMonthQueryDto> workMonthDto = workMonthEntityQueryRepository.findById(UUID.fromString(id));
        return workMonthDto.isPresent()
                ? ResponseEntity.ok(WorkMonthResponseConverter.convertWorkMonthResponseDto(workMonthDto.get()))
                : ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/api/workmonth/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<WorkMonthResponseDto> findByUser(@Valid @PathVariable @NotBlank @UniqueIdentifier String id) {
        final List<WorkMonthQueryDto> query = workMonthEntityQueryRepository.findAllByUserId(UUID.fromString(id));
        return query.stream()
                .map(WorkMonthResponseConverter::convertWorkMonthResponseDto)
                .collect(Collectors.toList());
    }
}