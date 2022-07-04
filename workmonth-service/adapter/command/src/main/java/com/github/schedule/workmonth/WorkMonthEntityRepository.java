package com.github.schedule.workmonth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

interface WorkMonthEntityRepository extends JpaRepository<WorkMonthEntity, UUID>  {
    Optional<WorkMonthEntity> findByUserIdAndDate(UUID id, YearMonth date);
}