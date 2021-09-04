package com.github.schedule.workmonth;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
class WorkMonthFacadeConfig {

    @Bean
    WorkMonthEntityConverter workMonthEntityConverter() {
        return new WorkMonthEntityConverter();
    }

    @Bean
    WorkMonthCreatedHandler workMonthCreatedHandler(WorkMonthEntityRepository workMonthEntityRepository) {
        return new WorkMonthCreatedHandler(workMonthEntityRepository);
    }

    @Bean
    WorkDaysChangedHandler workDaysChangedHandler(WorkMonthEntityRepository workMonthEntityRepository) {
        return new WorkDaysChangedHandler(workMonthEntityRepository);
    }

    @Bean
    TotalHoursCalculatedHandler totalHoursCalculatedHandler(WorkMonthEntityRepository workMonthEntityRepository) {
        return new TotalHoursCalculatedHandler(workMonthEntityRepository);
    }

    @Bean
    PersistenceWorkMonthHandler persistenceWorkMonthHandler(WorkMonthEntityRepository repository) {
        return new PersistenceWorkMonthHandler(workMonthCreatedHandler(repository), workDaysChangedHandler(repository), totalHoursCalculatedHandler(repository));
    }

    @Bean
    WorkMonthRepository workMonthRepository(WorkMonthEntityRepository repository, PersistenceWorkMonthHandler handler, WorkMonthEntityConverter converter) {
        return new JpaWorkMonthRepository(repository, handler, converter);
    }

}
