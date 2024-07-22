package com.oneliferp.cwu.Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;


public class SimpleDate {
    private static LocalDate getFirstMonthDay() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }
    private static LocalDate getLastMonthDay() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    private static LocalDate getFirstWeekDay() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
    private static LocalDate getLastWeekDay() {
        return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    public final int day;
    public final int month;
    public final int year;

    public final int hour;
    public final int minute;

    public SimpleDate(final int day, final int month, final int year, final int hour, final int minute) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d %02d:%02d", day, month, year, hour, minute);
    }

    public static SimpleDate now() {
        final LocalDateTime currentDateTime = LocalDateTime.now();
        return new SimpleDate(currentDateTime.getDayOfMonth(), currentDateTime.getMonthValue(), currentDateTime.getYear(), currentDateTime.getHour(), currentDateTime.getMinute());
    }

    public Temporal toTemporal() {
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    @JsonIgnore
    public static Duration between(final SimpleDate startInclusive, final SimpleDate endExclusive) {
        final Temporal startTemporal = startInclusive.toTemporal();
        final Temporal endTemporal = endExclusive.toTemporal();
        return Duration.between(startTemporal, endTemporal);
    }

    public boolean isWithinWeek() {
        final var date = LocalDate.of(this.year, this.month, this.day);
        final var firstWeekDay = getFirstWeekDay();
        final var lastWeekDay = getLastWeekDay();

        return (date.isEqual(firstWeekDay) || date.isAfter(firstWeekDay)) &&
                (date.isEqual(lastWeekDay) || date.isBefore(lastWeekDay));
    }

    public boolean isWithinMonth() {
        final var date = LocalDate.of(this.year, this.month, this.day);
        final var firstMonthDay = getFirstMonthDay();
        final var lastMonthDay = getLastMonthDay();

        return (date.isEqual(firstMonthDay) || date.isAfter(firstMonthDay)) &&
                (date.isEqual(lastMonthDay) || date.isBefore(lastMonthDay));
    }
}
