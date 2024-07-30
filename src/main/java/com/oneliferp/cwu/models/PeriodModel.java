package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.utils.SimpleDate;
import com.oneliferp.cwu.utils.SimpleDuration;

public class PeriodModel {
    @JsonProperty("startedAt")
    private SimpleDate startedAt;

    @JsonProperty("endedAt")
    private SimpleDate endedAt;

    @JsonProperty("duration")
    private SimpleDuration duration;

    public PeriodModel() {}

    /*
    Getters
    */
    public SimpleDate getStartedAt() {
        return this.startedAt;
    }

    public SimpleDate getEndedAt() {
        return this.endedAt;
    }

    public SimpleDuration getDuration() {
        return this.duration;
    }

    /*
    Methods
    */
    public void start() {
        this.startedAt = SimpleDate.now();
    }

    public void end() {
        this.endedAt = SimpleDate.now();
        this.duration = SimpleDate.between(this.startedAt, this.endedAt);
    }
}
