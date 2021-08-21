package com.github.schedule.workmonth.vo;

import com.github.schedule.workmonth.exception.WorkHourInvalidException;

import java.time.LocalDate;

public record WorkDay(LocalDate date, WorkHour startingHour, WorkHour endingHour, boolean isLeave) {

    public static final WorkHour STARTING_HOUR = WorkHour.zero();
    public static final WorkHour ENDING_HOUR = new WorkHour(23, 59);

    public WorkDay {
        checkWorkHour(startingHour);
        checkWorkHour(endingHour);
    }

    public WorkDay(LocalDate date, boolean leave) throws WorkHourInvalidException {
        this(date, WorkHour.zero(), WorkHour.zero(), leave);
    }

    public WorkHour calculateTotalTime() {
        return startingHour.difference(endingHour);
    }

    public boolean notGreaterThan(LocalDate date) {
        return this.date.isBefore(date) || this.date.isEqual(date);
    }

    public boolean notLesserThan(LocalDate date) {
        return this.date.isAfter(date) || this.date.isEqual(date);
    }

    public boolean same(LocalDate date) {
        return this.date.isEqual(date);
    }

    private void checkWorkHour(WorkHour workHour) {
        if(!(workHour.notLesserThan(STARTING_HOUR) && workHour.notGreaterThan(ENDING_HOUR))) {
            throw new WorkHourInvalidException();
        }
    }

}
