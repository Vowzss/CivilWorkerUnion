package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.utils.SimpleDateTime;
import com.oneliferp.cwu.utils.SimpleDuration;

public class PeriodModel {
    @JsonProperty("startedAt")
    private SimpleDateTime startedAt;

    @JsonProperty("endedAt")
    private SimpleDateTime endedAt;

    @JsonProperty("duration")
    private SimpleDuration duration;

    public PeriodModel() {
    }

    /* Getters */
    public SimpleDateTime getStartedAt() {
        return this.startedAt;
    }

    public SimpleDateTime getEndedAt() {
        return this.endedAt;
    }

    public SimpleDuration getDuration() {
        return this.duration;
    }

    /* Methods */
    public void start() {
        this.startedAt = SimpleDateTime.now();
    }

    public void end() {
        this.endedAt = SimpleDateTime.now();
        this.duration = SimpleDateTime.between(this.startedAt, this.endedAt);
    }
}
