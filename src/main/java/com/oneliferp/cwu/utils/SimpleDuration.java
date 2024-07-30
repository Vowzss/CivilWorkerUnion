package com.oneliferp.cwu.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;

public class SimpleDuration {

    @JsonIgnore
    private int hours;

    @JsonIgnore
    private int minutes;

    @JsonProperty("time")
    public final String time;

    public SimpleDuration(final int hour, final int minute) {
        this.hours = hour;
        this.minutes = minute;

        // Fix overflow
        this.normalize();

        this.time = String.format("%02d:%02d", this.hours, this.minutes);
    }

    public SimpleDuration(final LocalTime time) {
        this.hours = time.getHour();
        this.minutes = time.getMinute();

        this.time = String.format("%02d:%02d", this.hours, this.minutes);
    }

    /*
    Methods
    */
    private void normalize() {
        if (this.minutes < 0) {
            int hourAdjustment = (this.minutes / 60) - 1;
            this.hours += hourAdjustment;
            this.minutes = 60 + (this.minutes % 60);
        } else {
            this.hours += this.minutes / 60;
            this.minutes = this.minutes % 60;
        }
    }
}
