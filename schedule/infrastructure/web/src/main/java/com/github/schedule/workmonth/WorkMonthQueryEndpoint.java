package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkMonthQueryDto;
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

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Workmonth query endpoint")
class WorkMonthQueryEndpoint {

    private final WorkMonthQueryRepository workMonthEntityQueryRepository;

    @GetMapping(value = "/api/workmonth", produces = MediaType.APPLICATION_JSON_VALUE)
    List<WorkMonthQueryDto> findAll(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        return (year != null && month != null)
               ? workMonthEntityQueryRepository.findAllByDate(YearMonth.of(year, month))
               : workMonthEntityQueryRepository.findAll();

    }

    @GetMapping(value = "/api/workmonth/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> findById(@Valid @PathVariable @NotBlank @UniqueIdentifier String id) {
        final Optional<WorkMonthQueryDto> workMonthDto = workMonthEntityQueryRepository.findById(UUID.fromString(id));
        return workMonthDto.isPresent()
                ? ResponseEntity.ok(workMonthDto.get())
                : ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/api/workmonth/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<WorkMonthQueryDto> findByUser(@Valid @PathVariable @NotBlank @UniqueIdentifier String id) {
        return workMonthEntityQueryRepository.findAllByUserId(UUID.fromString(id));
    }
}