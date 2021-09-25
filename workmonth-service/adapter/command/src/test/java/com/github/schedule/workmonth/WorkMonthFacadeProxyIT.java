package com.github.schedule.workmonth;

import com.github.schedule.workmonth.command.WorkDaysChangeCommand;
import com.github.schedule.workmonth.command.WorkMonthCreateCommand;
import com.github.schedule.workmonth.event.TotalHoursCalculatedEvent;
import com.github.schedule.workmonth.event.WorkDaysChangedEvent;
import com.github.schedule.workmonth.event.WorkMonthCreatedEvent;
import com.github.schedule.workmonth.event.WorkMonthEvent;
import com.github.schedule.workmonth.vo.UserId;
import com.github.schedule.workmonth.vo.WorkDay;
import com.github.schedule.workmonth.vo.WorkHour;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest(classes = {WorkMonthFacadeProxy.class, WorkMonthEntityRepository.class})
@ComponentScan("com.github.schedule.workmonth")
@EnableAutoConfiguration
@EnableJpaRepositories
public class WorkMonthFacadeProxyIT {

    @Autowired
    private WorkMonthFacade workMonthFacade;

    @Autowired
    private WorkMonthEntityRepository workMonthEntityRepository;

    /*
        WorkMonth Events
     */

    private final Class<WorkMonthCreatedEvent> WORK_MONTH_CREATED_EVENT = WorkMonthCreatedEvent.class;
    private final Class<WorkDaysChangedEvent> WORK_DAYS_CHANGED_EVENT = WorkDaysChangedEvent.class;
    private final Class<TotalHoursCalculatedEvent> TOTAL_HOURS_CALCULATED_EVENT = TotalHoursCalculatedEvent.class;

    @Test
    @DisplayName("Should Create WorkMonth And Save Properly")
    @Transactional
    void shouldCreateWorkMonthAndSaveItProperlyInDataBase() {

        /*
            Perform "createWorkMonth" domain operation and count number of created elements
         */

        final long countBeforeSave = workMonthEntityRepository.count();

        final WorkMonthCreateCommand workMonthCreateCommand = new WorkMonthCreateCommand(YearMonth.now(), new UserId(UUID.randomUUID()));
        final WorkMonthEvent workMonthEvent = workMonthFacade.createWorkMonth(workMonthCreateCommand).get(0);

        final long countAfterSave = workMonthEntityRepository.count();

        /*
            Compare data from event and database
         */

        assertTrue(countAfterSave > countBeforeSave);
        assertEquals(1, countAfterSave - countBeforeSave);
        assertNotNull(workMonthEvent);
        assertEquals(WORK_MONTH_CREATED_EVENT, workMonthEvent.getClass());

        final WorkMonthCreatedEvent workMonthCreatedEvent = (WorkMonthCreatedEvent) workMonthEvent;
        final WorkMonthEntity workMonthEntity = workMonthEntityRepository.findAll().get(0);
        compareWorkMonthEntityAndWorkMonthCreatedEvent(workMonthCreatedEvent, workMonthEntity);

        workMonthCreatedEvent.workDays().forEach(workDay -> {
            final Optional<WorkDayEntity> found = workMonthEntity.getWorkDays().stream().filter(target -> target.getDate().isEqual(workDay.date())).findAny();
            assertTrue(found.isPresent());
            compareWorkDays(workDay, found.get());
        });
    }

    private void compareWorkMonthEntityAndWorkMonthCreatedEvent(WorkMonthCreatedEvent workMonthCreatedEvent, WorkMonthEntity workMonthEntity) {
        assertEquals(workMonthCreatedEvent.userId().id(), workMonthEntity.getUserId());
        assertEquals(workMonthCreatedEvent.yearMonth(), workMonthEntity.getDate());
        assertEquals(workMonthCreatedEvent.totalHours().hours(), workMonthEntity.getTotalHours().getHours());
        assertEquals(workMonthCreatedEvent.totalHours().minutes(), workMonthEntity.getTotalHours().getMinutes());
        assertEquals(workMonthCreatedEvent.workDays().size(), workMonthEntity.getWorkDays().size());
    }

    private void compareWorkDays(WorkDay workDay, WorkDayEntity workDayEntity) {
        assertEquals(workDay.date(), workDayEntity.getDate());
        assertEquals(workDay.isLeave(), workDayEntity.isLeave());
        assertEquals(workDay.startingHour().hours(), workDayEntity.getStartingHour().getHour());
        assertEquals(workDay.startingHour().minutes(), workDayEntity.getStartingHour().getMinute());
        assertEquals(workDay.endingHour().hours(), workDayEntity.getEndingHour().getHour());
        assertEquals(workDay.endingHour().minutes(), workDayEntity.getEndingHour().getMinute());
    }

    @Test
    @DisplayName("Should Update WorkDays For Given WorkMonth")
    @Transactional
    void shouldUpdateWorkDaysForGivenWorkMonth() {

        /*
            Create WorkMonth before changing WorkDays
         */

        final WorkMonthCreateCommand workMonthCreateCommand = new WorkMonthCreateCommand(YearMonth.now(), new UserId(UUID.randomUUID()));
        final WorkMonthCreatedEvent workMonthCreatedEvent = (WorkMonthCreatedEvent) workMonthFacade.createWorkMonth(workMonthCreateCommand).get(0);

        /*
            Perform changing WorkDays
         */

        final WorkDay workDay = new WorkDay(LocalDate.now(), new WorkHour(10, 10), new WorkHour(10, 10), false);
        final WorkDaysChangeCommand workDaysChangeCommand = new WorkDaysChangeCommand(workMonthCreatedEvent.aggregateId(), Set.of(workDay));
        final List<WorkMonthEvent> workMonthEvents = workMonthFacade.updateWorkDays(workDaysChangeCommand);

        /*
            Check 'updateWorkDays' events
         */

        assertEquals(WORK_DAYS_CHANGED_EVENT, workMonthEvents.get(0).getClass());
        assertEquals(TOTAL_HOURS_CALCULATED_EVENT, workMonthEvents.get(1).getClass());

        final Optional<WorkMonthEntity> workMonthEntity = workMonthEntityRepository.findById(workMonthCreatedEvent.aggregateId());
        assertTrue(workMonthEntity.isPresent());

        final Optional<WorkDayEntity> workDayEntity = workMonthEntity.get().getWorkDays().stream().filter(entity -> entity.getDate().isEqual(workDay.date())).findAny();
        final WorkDaysChangedEvent workDaysChangedEvent = (WorkDaysChangedEvent) workMonthEvents.get(0);
        final TotalHoursCalculatedEvent totalHoursCalculatedEvent = (TotalHoursCalculatedEvent) workMonthEvents.get(1);
        assertTrue(workDayEntity.isPresent());

        /*
            Compare WorkDayEntity with WorkDaysChangedEvent
         */

        final WorkDayEntity foundEntity = workDayEntity.get();
        final WorkDay foundWorkDay = workDaysChangedEvent.workDays().stream().findFirst().get();
        compareWorkDays(foundWorkDay, foundEntity);

        /*
            Compare WorkDayEntity with TotalHoursCalculatedEvent
         */

        final WorkMonthEntity foundWorkMonthEntity = workMonthEntity.get();
        final WorkHour foundWorkHour = totalHoursCalculatedEvent.totalWorkHours();
        assertEquals(foundWorkHour.hours(), foundWorkMonthEntity.getTotalHours().getHours());
        assertEquals(foundWorkHour.minutes(), foundWorkMonthEntity.getTotalHours().getMinutes());
    }

}
