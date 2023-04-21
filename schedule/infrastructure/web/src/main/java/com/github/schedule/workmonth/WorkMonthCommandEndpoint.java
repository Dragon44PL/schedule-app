package com.github.schedule.workmonth;

import com.github.schedule.workmonth.command.WorkDaysChangeCommand;
import com.github.schedule.workmonth.command.WorkMonthCreateCommand;
import com.github.schedule.workmonth.converter.WorkMonthRequestConverter;
import com.github.schedule.workmonth.dto.request.workday.WorkDaysChangeDto;
import com.github.schedule.workmonth.dto.request.workmonth.WorkMonthCreateDto;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.validation.constraint.UniqueIdentifier;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Workmonth command endpoint")
class WorkMonthCommandEndpoint {

    private final WorkMonthFacade workMonthFacade;

    @PostMapping(value = "/api/workmonth", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createWorkMonth(@RequestBody @Valid WorkMonthCreateDto workMonthCreateDto) {
        final WorkMonthCreateCommand workMonthCreateCommand = WorkMonthRequestConverter.convertWorkMonthCreateCommand(workMonthCreateDto);
        final List<WorkMonthEvent> workMonthEvents = workMonthFacade.createWorkMonth(workMonthCreateCommand);
        return workMonthEvents.size() != 0
                ? redirect(workMonthEvents.get(0).aggregateId(), HttpStatus.CREATED)
                : ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/api/workmonth/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> changeWorkDays(@Valid @PathVariable @NotBlank @UniqueIdentifier String id, @Valid @RequestBody WorkDaysChangeDto workDaysChangeDto) {
        final WorkDaysChangeCommand workDaysChangeCommand = WorkMonthRequestConverter.convertWorkDaysChangedCommand(id, workDaysChangeDto);
        final List<WorkMonthEvent> workMonthEvents = workMonthFacade.updateWorkDays(workDaysChangeCommand);
        return workMonthEvents.size() != 0
                ? redirect(workMonthEvents.get(0).aggregateId(), HttpStatus.OK)
                : ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> redirect(UUID id, HttpStatus httpStatus) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/workmonth/%s".formatted(id.toString()));
        return new ResponseEntity<>(headers, httpStatus);
    }
}
