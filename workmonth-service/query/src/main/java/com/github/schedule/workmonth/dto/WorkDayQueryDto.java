package com.github.schedule.workmonth.dto;

import java.time.LocalDate;

public record WorkDayQueryDto(LocalDate date, WorkHourQueryDto startingHour, WorkHourQueryDto endingHour, boolean isLeave) { }
