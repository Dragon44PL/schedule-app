package com.github.schedule.workmonth;

import com.github.schedule.workmonth.command.WorkDaysChangeCommand;
import com.github.schedule.workmonth.command.WorkMonthCreateCommand;
import com.github.schedule.workmonth.converter.WorkMonthRequestConverter;
import com.github.schedule.workmonth.dto.request.workday.WorkDaysChangeDto;
import com.github.schedule.workmonth.dto.request.workmonth.WorkMonthCreateDto;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.validation.constraint.UniqueIdentifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.print.attribute.standard.Media;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Validated
@Controller
class WorkMonthCommandEndpoint {

    private final WorkMonthFacade workMonthFacade;
    private final WorkMonthRequestConverter workMonthRequestConverter;

    WorkMonthCommandEndpoint(WorkMonthFacade workMonthFacade, WorkMonthRequestConverter workMonthRequestConverter) {
        this.workMonthFacade = workMonthFacade;
        this.workMonthRequestConverter = workMonthRequestConverter;
    }

    @PostMapping(value = "/api/workmonth", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createWorkMonth(@RequestBody @Valid WorkMonthCreateDto workMonthCreateDto) {
        final WorkMonthCreateCommand workMonthCreateCommand = workMonthRequestConverter.convertWorkMonthCreateCommand(workMonthCreateDto);
        final List<WorkMonthEvent> workMonthEvents = workMonthFacade.createWorkMonth(workMonthCreateCommand);
        return workMonthEvents.size() != 0
                ? redirect(workMonthEvents.get(0).aggregateId(), HttpStatus.CREATED)
                : ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/api/workmonth/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> changeWorkDays(@Valid @PathVariable @NotBlank @UniqueIdentifier String id, @Valid @RequestBody WorkDaysChangeDto workDaysChangeDto) {
        final WorkDaysChangeCommand workDaysChangeCommand = workMonthRequestConverter.convertWorkDaysChangedCommand(id, workDaysChangeDto);
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
