package com.oneliferp.cwu.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.*;
import java.time.temporal.TemporalAdjusters;


public class SimpleDate implements Comparable<SimpleDate> {
    @JsonIgnore
    private final int day;

    @JsonIgnore
    private final int month;

    @JsonIgnore
    private final int year;

    @JsonIgnore
    private final int hour;

    @JsonIgnore
    private final int minute;

    @JsonProperty("date")
    private final String date;

    @JsonProperty("time")
    private final String time;

    public SimpleDate(final int day, final int month, final int year, final int hour, final int minute) {
        this.day = day;
        this.month = month;
        this.year = year;

        this.hour = hour;
        this.minute = minute;

        this.date = String.format("%02d/%02d/%04d", day, month, year);
        this.time = String.format("%02d:%02d", hour, minute);
    }

    public SimpleDate(final LocalDate date, final LocalTime time) {
        this.day = date.getDayOfMonth();
        this.month = date.getMonthValue();
        this.year = date.getYear();

        this.hour = time.getHour();
        this.minute = time.getMinute();

        this.date = String.format("%02d/%02d/%04d", day, month, year);
        this.time = String.format("%02d:%02d", hour, minute);
    }

    /* Getters */
    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    /* Utils */
    public static SimpleDate now() {
        final LocalDateTime currentDateTime = LocalDateTime.now();
        return new SimpleDate(currentDateTime.getDayOfMonth(), currentDateTime.getMonthValue(), currentDateTime.getYear(), currentDateTime.getHour(), currentDateTime.getMinute());
    }

    public static SimpleDuration between(final SimpleDate startInclusive, final SimpleDate endExclusive) {
        final int hoursDifference = endExclusive.hour - startInclusive.hour;
        final int minutesDifference = endExclusive.minute - startInclusive.minute;

        return new SimpleDuration(hoursDifference, minutesDifference);
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

    public boolean isAfter(final SimpleDate other) {
        return this.compareTo(other) > 0;
    }

    public boolean isBefore(final SimpleDate other) {
        return this.compareTo(other) < 0;
    }

    private boolean isEqual(final SimpleDate other) {
        return this.compareTo(other) == 0;
    }

    /* Helpers */
    public static LocalDate getFirstMonthDay() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getLastMonthDay() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getFirstWeekDay() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public static LocalDate getLastWeekDay() {
        return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    @Override
    public int compareTo(final SimpleDate other) {
        if (this.year != other.year) {
            return Integer.compare(this.year, other.year);
        }
        if (this.month != other.month) {
            return Integer.compare(this.month, other.month);
        }
        if (this.day != other.day) {
            return Integer.compare(this.day, other.day);
        }
        if (this.hour != other.hour) {
            return Integer.compare(this.hour, other.hour);
        }
        return Integer.compare(this.minute, other.minute);
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.date, this.time);
    }
}
