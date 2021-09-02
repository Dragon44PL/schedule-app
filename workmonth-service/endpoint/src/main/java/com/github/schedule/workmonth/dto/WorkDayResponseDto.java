package com.github.schedule.workmonth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkDayResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD")
    private LocalDate date;

    private WorkDayHourResponseDto startingHour;

    private WorkDayHourResponseDto endingHour;

    private boolean isLeave;
}
