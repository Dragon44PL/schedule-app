package com.github.schedule.workmonth;

import org.springframework.data.repository.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface WorkMonthEntityQueryRepository extends Repository<WorkMonthEntity, UUID> {
    List<WorkMonthEntity> findAll();
    List<WorkMonthEntity> findAllByDate(YearMonth yearMonth);
    Optional<WorkMonthEntity> findById(UUID id);
    List<WorkMonthEntity> findAllByUserId(UUID userId);
}
