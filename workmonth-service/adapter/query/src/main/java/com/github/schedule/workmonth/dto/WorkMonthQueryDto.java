package com.github.schedule.workmonth.dto;

import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

public record WorkMonthQueryDto(UUID id, UUID userId, YearMonth date, WorkHourQueryDto totalHours, Set<WorkDayQueryDto> workDays) { }
