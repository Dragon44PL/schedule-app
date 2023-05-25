package com.github.schedule.workmonth.dto;

import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.vo.WorkHour;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class TotalHoursCalculatedKafkaEventDto {

    private Instant occurredOn;
    private UUID aggregateId;
    private WorkHour totalWorkHours;

    public static TotalHoursCalculatedKafkaEventDto from(TotalHoursCalculatedEvent event) {
        return TotalHoursCalculatedKafkaEventDto.builder()
                .occurredOn(event.occurredOn())
                .aggregateId(event.aggregateId())
                .build();
    }
}
