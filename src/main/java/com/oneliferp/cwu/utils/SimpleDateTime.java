package com.oneliferp.cwu.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleDateTime implements Comparable<SimpleDateTime> {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH'h'mm");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @JsonIgnore
    private final int day;

    @JsonIgnore
    private final int month;

    @JsonIgnore
    private final int year;

    @JsonIgnore
    private final Integer hour;

    @JsonIgnore
    private final Integer minute;

    @JsonProperty("date")
    private final String date;

    @JsonProperty("time")
    private String time;

    public SimpleDateTime(final int day, final int month, final int year, final int hour, final int minute) {
        this.day = day;
        this.month = month;
        this.year = year;

        this.hour = hour;
        this.minute = minute;

        this.date = this.formatToDate();
        this.time = this.formatToTime();
    }

    public SimpleDateTime(final LocalDate date, final LocalTime time) {
        this.day = date.getDayOfMonth();
        this.month = date.getMonthValue();
        this.year = date.getYear();

        if (time != null) {
            this.hour = time.getHour();
            this.minute = time.getMinute();
        } else {
            this.hour = null;
            this.minute = null;
        }

        this.date = this.formatToDate();
        this.time = this.formatToTime();
    }

    public SimpleDateTime(final LocalDateTime dateTime) {
        this(dateTime.toLocalDate(), dateTime.toLocalTime());
    }

    public SimpleDateTime(final LocalDate date) {
        this(date, null);
    }

    /* Getters */
    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    /* Utils */
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

    public boolean isAfter(final SimpleDateTime other) {
        return this.compareTo(other) > 0;
    }

    public boolean isBefore(final SimpleDateTime other) {
        return this.compareTo(other) < 0;
    }

    public boolean isEqual(final SimpleDateTime other) {
        return this.compareTo(other) == 0;
    }

    public LocalDateTime toLocalDateTime() {
        if (this.hour == null || this.minute == null) return LocalDateTime.of(this.year, this.month, this.day, 0, 0);
        return LocalDateTime.of(this.year, this.month, this.day, this.hour, this.minute);
    }

    /* Object */
    @Override
    public int compareTo(final SimpleDateTime other) {
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
        return String.format("Le %s à %s", this.date, this.time);
    }

    /* Utils */
    private String formatToDate() {
        return String.format("%02d/%02d/%04d", this.day, this.month, this.year);
    }

    private String formatToTime() {
        if (this.hour == null || this.minute == null) return null;
        return String.format("%02d:%02d", this.hour, this.minute);
    }

    /* Helpers */
    public static SimpleDateTime now() {
        final LocalDateTime currentDateTime = LocalDateTime.now();
        return new SimpleDateTime(currentDateTime.getDayOfMonth(), currentDateTime.getMonthValue(), currentDateTime.getYear(), currentDateTime.getHour(), currentDateTime.getMinute());
    }

    public static SimpleDuration between(final SimpleDateTime startInclusive, final SimpleDateTime endExclusive) {
        final int hoursDifference = endExclusive.hour - startInclusive.hour;
        final int minutesDifference = endExclusive.minute - startInclusive.minute;

        return new SimpleDuration(hoursDifference, minutesDifference);
    }

    public static SimpleDateTime parseDate(final String str) {
        try {
            return new SimpleDateTime(LocalDateTime.parse(str, DATE_TIME_FORMATTER));
        } catch (final DateTimeParseException e) {
            return new SimpleDateTime(LocalDate.parse(str, DATE_FORMATTER));
        }
    }

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
}
