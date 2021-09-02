package com.github.schedule.workmonth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkMonthHourResponseDto {
    private int hours;
    private int minutes;
}
