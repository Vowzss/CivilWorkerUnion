package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.database.CwuDatabase;
import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.misc.ZoneType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionModel {
    @JsonProperty("manager")
    private IdentityModel manager;

    @JsonProperty("participants")
    private HashMap<ParticipantType, List<IdentityModel>> participants;

    @JsonProperty("type")
    private SessionType type;

    @JsonProperty("zone")
    private ZoneType zone;

    @JsonProperty("information")
    private String info;

    @JsonProperty("period")
    private PeriodModel period;

    @JsonProperty("income")
    private IncomeModel income;

    @JsonIgnore
    public PageType currentPage;

    public SessionModel() {
    }

    public SessionModel(final CwuModel cwu) {
        this.period = new PeriodModel();
        this.period.start();

        this.manager = cwu.getIdentity();
    }

    /*
    Setters
    */
    public void addParticipant(final ParticipantType type, final IdentityModel identity) {
        this.participants.get(type).add(identity);
    }

    public void addParticipants(final ParticipantType type, final List<IdentityModel> identities) {
        this.participants.get(type).addAll(identities);
    }

    public void clearParticipants(final ParticipantType type) {
        this.participants.get(type).clear();
    }

    public void setType(final SessionType type) {
        this.type = type;
    }

    public void setZone(final ZoneType zone) {
        this.zone = zone;
    }

    public void setInfo(@Nullable final String info) {
        this.info = (info == null || info.isBlank()) ? null : info;
    }

    public void setEarnings(final Integer earnings) {
        this.income.setEarnings(earnings);
    }

    /*
    Getters
    */
    public String getManagerCid() {
        return this.manager.cid;
    }

    public List<IdentityModel> getParticipants(final ParticipantType type) {
        return this.participants.get(type);
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

    public PeriodModel getPeriod() {
        return this.period;
    }

    public IncomeModel getIncome() {
        return this.income;
    }

    /*
    Methods
    */
    public void begin() {
        this.period.start();
        this.income = new IncomeModel();
        this.participants = new HashMap<>();

        // Prefill participants with available types
        for (final ParticipantType type : ParticipantType.values()) {
            this.participants.put(type, new ArrayList<>());
        }

        this.currentPage = PageType.LOYALISTS;
    }

    public void end() {
        this.period.end();
        this.computeWages();
        this.computeDeposit();
    }

    public boolean isValid() {
        return this.manager != null && this.type != null && this.zone != null && this.hasParticipants();
    }

    public void computeWages() {
        final AtomicInteger wages = new AtomicInteger(
                participants.entrySet().stream()
                        .mapToInt(entry -> entry.getKey().getWage() * entry.getValue().size())
                        .sum()
        );

        this.income.setWages(wages.get());
    }

    public void computeDeposit() {
        this.income.setDeposit(this.income.getDeposit() - this.income.getWages());
    }

    /*
    Utils
    */
    private boolean hasParticipants() {
        boolean isValid = false;

        for (final ParticipantType type : ParticipantType.values()) {
            if (this.participants.get(type).isEmpty()) continue;
            isValid = true;
        }

        return isValid;
    }

    public CwuModel resolveCwu() {
        return CwuDatabase.getInstance().getFromCid(this.manager.cid);
    }

    public boolean isWithinWeek() {
        return this.period.getStartedAt().isWithinWeek();
    }

    public boolean isWithinMonth() {
        return this.period.getStartedAt().isWithinMonth();
    }

    public boolean isFirstPage() {
        return this.currentPage.ordinal() == 0;
    }

    public boolean isLastPage() {
        return this.currentPage.ordinal() == PageType.values().length - 2;
    }
}
