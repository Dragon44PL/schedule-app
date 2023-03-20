package com.github.schedule.workmonth.vo;

public record WorkHour(int hours, int minutes) {

    public static WorkHour zero() {
        return new WorkHour(0, 0);
    }

    public WorkHour difference(WorkHour another) {
        final int totalMinutes = minutes - another.minutes;
        final int additionalHours = minutes < 0 ? absolute(totalMinutes) % 60 : 0;
        return new WorkHour(absolute(hours - another.hours) - additionalHours, absolute(totalMinutes));
    }

    public WorkHour add(WorkHour another) {
        final int totalMinutes = minutes + another.minutes;
        return new WorkHour(hours + another.hours + (totalMinutes / 60), totalMinutes % 60);
    }

    private int absolute(int value) {
        return (value > 0) ? value : -value;
    }

    public boolean notGreaterThan(WorkHour another) {
        return hours < another.hours || (hours == another.hours && (minutes < another.minutes || minutes == another.minutes));
    }

    public boolean notLesserThan(WorkHour another) {
        return hours > another.hours || (hours == another.hours && (minutes > another.minutes || minutes == another.minutes));
    }
}