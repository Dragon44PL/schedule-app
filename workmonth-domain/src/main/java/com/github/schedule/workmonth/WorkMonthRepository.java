package com.github.schedule.workmonth;

import com.github.schedule.core.DomainRepository;
import com.github.schedule.workmonth.vo.UserId;

import java.util.Optional;
import java.util.UUID;

interface WorkMonthRepository extends DomainRepository<UUID, WorkMonth> {
    Optional<WorkMonth> findByUserId(UserId userId);
}