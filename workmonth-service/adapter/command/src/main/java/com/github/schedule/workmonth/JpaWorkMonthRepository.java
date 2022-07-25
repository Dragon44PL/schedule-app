package com.github.schedule.workmonth;

import com.github.schedule.workmonth.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class JpaWorkMonthRepository implements WorkMonthRepository {

    private final WorkMonthEntityRepository workMonthEntityRepository;
    private final PersistenceWorkMonthHandler persistenceWorkMonthHandler;

    public Optional<WorkMonth> findById(UUID uuid) {
        Optional<WorkMonthEntity> workMonthEntity = workMonthEntityRepository.findById(uuid);
        return workMonthEntity.map(WorkMonthEntityConverter::convertWorkMonth);
    }

    public void save(WorkMonth workMonth) {
        workMonth.events().forEach(persistenceWorkMonthHandler::handle);
    }

    public Optional<WorkMonth> findByUser(UserId userId, YearMonth yearMonth) {
        Optional<WorkMonthEntity> workMonthEntity = workMonthEntityRepository.findByUserIdAndDate(userId.id(), yearMonth);
        return workMonthEntity.map(WorkMonthEntityConverter::convertWorkMonth);
    }
}
