package com.github.schedule.workmonth;

import com.github.schedule.workmonth.vo.UserId;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

class JpaWorkMonthRepository implements WorkMonthRepository {

    private final WorkMonthEntityRepository workMonthEntityRepository;
    private final PersistenceWorkMonthHandler persistenceWorkMonthHandler;
    private final WorkMonthEntityConverter workMonthEntityConverter;

    public JpaWorkMonthRepository(WorkMonthEntityRepository workMonthEntityRepository, PersistenceWorkMonthHandler persistenceWorkMonthHandler, WorkMonthEntityConverter workMonthEntityConverter) {
        this.workMonthEntityRepository = workMonthEntityRepository;
        this.persistenceWorkMonthHandler = persistenceWorkMonthHandler;
        this.workMonthEntityConverter = workMonthEntityConverter;
    }

    public Optional<WorkMonth> findById(UUID uuid) {
        Optional<WorkMonthEntity> workMonthEntity = workMonthEntityRepository.findById(uuid);
        return workMonthEntity.map(workMonthEntityConverter::convertWorkMonth);
    }

    public void save(WorkMonth workMonth) {
        workMonth.events().forEach(persistenceWorkMonthHandler::handle);
    }

    public Optional<WorkMonth> findByUser(UserId userId, YearMonth yearMonth) {
        Optional<WorkMonthEntity> workMonthEntity = workMonthEntityRepository.findByUserIdAndDate(userId.id(), yearMonth);
        return workMonthEntity.map(workMonthEntityConverter::convertWorkMonth);
    }
}
