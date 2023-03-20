package com.github.schedule.workmonth.dto;

import com.github.schedule.workmonth.vo.WorkDay;

import java.util.Set;
import java.util.UUID;

public record WorkDaysChangeCommand(UUID id, Set<WorkDay> workDays) { }
