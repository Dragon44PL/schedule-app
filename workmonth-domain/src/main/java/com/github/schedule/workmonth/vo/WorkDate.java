package com.github.schedule.workmonth.vo;

import java.time.LocalDate;

public record WorkDate(int year, int month) {

    public static WorkDate of(LocalDate date) {
        return new WorkDate(date.getYear(), date.getMonth().getValue());
    }

}