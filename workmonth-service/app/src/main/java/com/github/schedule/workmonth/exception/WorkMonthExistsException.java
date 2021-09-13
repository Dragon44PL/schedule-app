package com.github.schedule.workmonth.exception;

public class WorkMonthExistsException extends RuntimeException {

    public WorkMonthExistsException(String message) {
        super(message);
    }

    public WorkMonthExistsException() {
        super();
    }

}