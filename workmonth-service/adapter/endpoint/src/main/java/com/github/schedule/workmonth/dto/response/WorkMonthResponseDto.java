package com.github.schedule.workmonth.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkMonthResponseDto {

    private UUID id;

    private UUID userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private YearMonth yearMonth;

    private WorkMonthHourResponseDto totalHours;

    private Set<WorkDayResponseDto> workDays;
}
