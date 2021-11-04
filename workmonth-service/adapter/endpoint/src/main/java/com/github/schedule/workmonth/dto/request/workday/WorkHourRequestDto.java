package com.github.schedule.workmonth.dto.request.workday;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkHourRequestDto {
    private int hour;
    private int minute;
}
