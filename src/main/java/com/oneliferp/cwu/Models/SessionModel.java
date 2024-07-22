package com.oneliferp.cwu.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.Utils.SimpleDate;
import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.ZoneType;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class SessionModel {

    private final CwuModel cwu;

    @JsonProperty("loyalists")
    private final HashMap<String, String> loyalists;

    @JsonProperty("citizens")
    private final HashMap<String, String> citizens;

    @JsonProperty("vortigaunts")
    private final HashMap<String, String> vortigaunts;

    @JsonProperty("anti_citizens")
    private final HashMap<String, String> antiCitizens;

    @JsonProperty("startedAt")
    private final SimpleDate startedAt;

    @JsonProperty("endedAt")
    private SimpleDate endedAt;

    @JsonIgnore()
    private Duration duration;

    @JsonProperty("type")
    private SessionType type;

    @JsonProperty("zone")
    private ZoneType zone;

    @JsonProperty("information")
    private String info = "";

    @JsonIgnore
    private int earnings = 0;

    @JsonIgnore
    private int wages = 0;

    @JsonIgnore
    private int deposit = 0;

    @JsonIgnore
    public PageType currentPage;

    public SessionModel(final CwuModel cwu) {
        this.startedAt = SimpleDate.now();

        this.cwu = cwu;
        this.loyalists = new HashMap<>();
        this.citizens = new HashMap<>();
        this.vortigaunts = new HashMap<>();
        this.antiCitizens = new HashMap<>();

        this.currentPage = PageType.LOYALISTS;
    }

    /*
    Setters
    */
    public void addLoyalist(final String name, final String cid) {
        this.loyalists.put(name, cid);
    }

    public void addCitizen(final String name, final String cid) {
        this.citizens.put(name, cid);
    }

    public void addVortigaunt(final String name, final String cid) {
        this.vortigaunts.put(name, cid);
    }

    public void addAntiCitizen(final String name, final String cid) {
        this.antiCitizens.put(name, cid);
    }

    public void clearParticipants(final ParticipantType participantType) {
        switch (participantType) {
            case LOYALIST -> this.loyalists.clear();
            case CITIZEN  -> this.citizens.clear();
            case VORTIGAUNT -> this.vortigaunts.clear();
            case ANTI_CITIZEN  -> this.antiCitizens.clear();
        }
    }

    public void setType(final SessionType type) {
        this.type = type;
    }

    public void setZone(final ZoneType zone) {
        this.zone = zone;
    }

    public void setInfo(final String info) {
        this.info = info;
    }

    public void setEarnings(final Integer earnings) {
        this.earnings = earnings;
    }

    /*
    Getters
    */

    public CwuModel getCwu() {
        return this.cwu;
    }

    public HashMap<String, String> getLoyalists() {
        return this.loyalists;
    }

    public HashMap<String, String> getCitizens() {
        return this.citizens;
    }

    public HashMap<String, String> getVortigaunts() {
        return this.vortigaunts;
    }

    public HashMap<String, String> getAntiCitizens() {
        return this.antiCitizens;
    }

    public SessionType getType() {
        return this.type;
    }

    public ZoneType getZone() {
        return this.zone;
    }

    public String getInfo() {
        return this.info;
    }

    public Integer getEarnings() {
        return this.earnings;
    }

    public boolean isWithinWeek() {
        return this.startedAt.isWithinWeek();
    }

    public boolean isWithinMonth() {
        return this.startedAt.isWithinMonth();
    }

    /*
    Methods
    */
    public boolean isValid() {
        this.endedAt = SimpleDate.now();
        this.duration = SimpleDate.between(this.startedAt, this.endedAt);

        return this.cwu != null && this.type != null && this.zone != null && (!this.loyalists.isEmpty() || !this.citizens.isEmpty() || !this.vortigaunts.isEmpty() || !this.antiCitizens.isEmpty());
    }
}
