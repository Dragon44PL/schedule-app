package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkDayQueryDto;
import com.github.schedule.workmonth.dto.WorkHourQueryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class FlattenedWorkMonthCollection {
    private UUID id;
    private UUID userId;
    private Integer year;
    private Integer month;
    private LocalDateTime date;
    private Integer hours;
    private Integer minutes;
    private LocalDateTime startingHour;
    private LocalDateTime endingHour;
    private Boolean isLeave;

    WorkDayQueryDto toWorkDayDto() {
        return new WorkDayQueryDto(date.toLocalDate(), toWorkHour(startingHour), toWorkHour(endingHour), isLeave);
    }

    private static WorkHourQueryDto toWorkHour(LocalDateTime localDateTime) {
       return (localDateTime != null) ? new WorkHourQueryDto(localDateTime.getHour(), localDateTime.getMonthValue()) : null;
    }
}