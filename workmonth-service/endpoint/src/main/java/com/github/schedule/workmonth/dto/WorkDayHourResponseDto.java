package com.github.schedule.workmonth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkDayHourResponseDto {
    private int hour;
    private int minute;
}
