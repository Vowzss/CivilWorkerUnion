package com.oneliferp.cwu.Models;

import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.ZoneType;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class SessionModel {
    private final CwuModel cwu;

    private final HashMap<String, String> loyalists;
    private final HashMap<String, String> citizens;
    private final HashMap<String, String> vortigaunts;

    private final HashMap<String, String> antiCitizens;

    private final Instant startDate;
    private Instant endDate;

    private Duration duration;

    private SessionType type;
    private ZoneType zone;

    private String info = "";

    private int earnings = 0;
    private int wages = 0;
    private int deposit = 0;

    public PageType currentPage;

    public SessionModel(final CwuModel cwu) {
        this.startDate = Instant.now();

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

    /*
    Methods
    */
    public boolean validate() {
        final int citizenCount = loyalists.size() + citizens.size() + vortigaunts.size();
        final int antiCitizenCount = antiCitizens.size();

        this.wages = citizenCount * 60 + antiCitizenCount * 40;
        this.deposit = this.earnings - this.wages;

        this.endDate = Instant.now();
        this.duration = Duration.between(this.startDate, this.endDate);

        return this.cwu != null && this.type != null && this.zone != null;
    }
}
