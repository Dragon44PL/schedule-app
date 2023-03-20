package com.github.schedule.workmonth.dto;

import com.github.schedule.workmonth.vo.UserId;

import java.time.YearMonth;

public record WorkMonthCreateCommand(YearMonth yearMonth, UserId userId) { }