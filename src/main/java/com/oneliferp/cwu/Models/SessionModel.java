package com.oneliferp.cwu.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneliferp.cwu.Utils.SimpleDate;
import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.ZoneType;
import jdk.jfr.Name;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class SessionModel {
    @Expose
    @SerializedName("cwu")
    private final String cwuIdentity;

    @Expose
    @SerializedName("loyalists")
    private final HashMap<String, String> loyalists;

    @Expose
    @SerializedName("citizens")
    private final HashMap<String, String> citizens;

    @Expose
    @SerializedName("vortigaunts")
    private final HashMap<String, String> vortigaunts;

    @Expose
    @SerializedName("anti_citizens")
    private final HashMap<String, String> antiCitizens;

    @Expose
    @SerializedName("startedAt")
    private final SimpleDate startedAt;

    @Expose
    @SerializedName("endedAt")
    private SimpleDate endedAt;

    private Duration duration;

    @Expose
    @SerializedName("type")
    private SessionType type;

    @Expose
    @SerializedName("zone")
    private ZoneType zone;

    @Expose
    @SerializedName("information")
    private String info = "";

    private int earnings = 0;

    private int wages = 0;

    private int deposit = 0;

    public PageType currentPage;

    public SessionModel(final CwuModel cwu) {
        this.startedAt = SimpleDate.now();

        this.cwuIdentity = cwu.getIdentity();
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

    public String getCwuIdentity() {
        return this.cwuIdentity;
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

        return this.cwuIdentity != null && this.type != null && this.zone != null && (!this.loyalists.isEmpty() || !this.citizens.isEmpty() || !this.vortigaunts.isEmpty() || !this.antiCitizens.isEmpty());
    }
}
