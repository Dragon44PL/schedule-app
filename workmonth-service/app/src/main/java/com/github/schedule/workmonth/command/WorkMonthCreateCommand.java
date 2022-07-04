package com.github.schedule.workmonth.command;

import com.github.schedule.workmonth.vo.UserId;

import java.time.YearMonth;

public record WorkMonthCreateCommand(YearMonth yearMonth, UserId userId) { }