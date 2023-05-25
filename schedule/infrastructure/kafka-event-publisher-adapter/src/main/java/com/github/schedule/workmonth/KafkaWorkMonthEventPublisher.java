package com.github.schedule.workmonth;

import com.github.schedule.core.events.DomainEventPublisher;
import com.github.schedule.workmonth.dto.TotalHoursCalculatedKafkaEventDto;
import com.github.schedule.workmonth.dto.WorkMonthCreatedEventDto;
import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class KafkaWorkMonthEventPublisher implements DomainEventPublisher<WorkMonthEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${schedule.workmonths.events.kafka.topic}")
    private String topicName;

    @Override
    public void publish(WorkMonthEvent workMonthEvent) {

        // Publish events for calculated hours only right now
        // TODO add other events
        final Object event = switch (workMonthEvent) {
            case TotalHoursCalculatedEvent e -> TotalHoursCalculatedKafkaEventDto.from(e);
            default -> null;
        };

        if (event != null) {
           kafkaTemplate.send(topicName, event);
           log.info("WorkMonth event = '{}' has been sent to '{}' topic");
        }
    }
}
