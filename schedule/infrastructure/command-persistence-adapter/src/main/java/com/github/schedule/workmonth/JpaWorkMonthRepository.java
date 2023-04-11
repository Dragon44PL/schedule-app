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
    private final WorkMonthEventHandler workMonthEventHandler;

    public Optional<WorkMonth> findById(UUID uuid) {
        final Optional<WorkMonthEntity> workMonthEntity = workMonthEntityRepository.findById(uuid);
        return workMonthEntity.map(WorkMonthEntityConverter::convertWorkMonth);
    }

    public void save(WorkMonth workMonth) {
        workMonth.events().forEach(workMonthEventHandler::handle);
    }

    public Optional<WorkMonth> findByUser(UserId userId, YearMonth yearMonth) {
        final Optional<WorkMonthEntity> workMonthEntity = workMonthEntityRepository.findByUserIdAndYearMonth(userId.id(), new YearMonthEntity(yearMonth.getYear(), yearMonth.getMonthValue()));
        return workMonthEntity.map(WorkMonthEntityConverter::convertWorkMonth);
    }
}
