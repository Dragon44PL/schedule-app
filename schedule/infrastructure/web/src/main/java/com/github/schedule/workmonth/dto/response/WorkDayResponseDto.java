package com.github.schedule.workmonth.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkDayResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd")
    private LocalDate date;

    private WorkDayHourResponseDto startingHour;

    private WorkDayHourResponseDto endingHour;

    private boolean isLeave;
}
