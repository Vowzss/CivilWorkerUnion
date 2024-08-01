package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.database.CwuDatabase;
import com.oneliferp.cwu.misc.PageType;
import com.oneliferp.cwu.misc.ParticipantType;
import com.oneliferp.cwu.misc.SessionType;
import com.oneliferp.cwu.misc.ZoneType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionModel {
    @JsonProperty("manager")
    private IdentityModel manager;

    @JsonProperty("participants")
    private HashMap<ParticipantType, HashSet<IdentityModel>> participants;

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
    private PageType currentPage;

    @JsonIgnore
    private boolean canSelectZone;

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
    public void addParticipants(final ParticipantType type, final List<IdentityModel> identities) {
        final var entries = this.participants.get(type);
        for (final IdentityModel identity : identities) {
            if (entries.contains(identity)) continue;
            entries.add(identity);
        }
    }

    public void clearParticipants(final ParticipantType type) {
        this.participants.get(type).clear();
    }

    public void setType(final SessionType type) {
        this.type = type;
    }

    public void setZone(final ZoneType zone) {
        this.zone = zone;
        this.canSelectZone = zone == ZoneType.UNKNOWN;
    }

    public void setInfo(@Nullable final String info) {
        this.info = (info == null || info.isBlank()) ? null : info;
    }

    public void setEarnings(final Integer earnings) {
        this.income.setEarnings(earnings);
    }

    public void setCurrentPage(final PageType page) {
        this.currentPage = page;
    }

    /*
    Getters
    */
    public String getManagerCid() {
        return this.manager.cid;
    }

    public HashSet<IdentityModel> getParticipants(final ParticipantType type) {
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

    public PageType getCurrentPage() {
        return this.currentPage;
    }

    public int getCurrentStep() {
        return this.canSelectZone ? currentPage.ordinal() + 1 : currentPage.ordinal();
    }

    public int getMaxSteps() {
        return PageType.values().length - (this.canSelectZone ? 1 : 2) - (this.type.isProfitable() ? 0 : 1);
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
            this.participants.put(type, new HashSet<>());
        }

        this.currentPage = this.canSelectZone ? PageType.ZONE : PageType.LOYALISTS;
    }

    public void end() {
        this.period.end();

        if (this.income.getEarnings() == null) {
            this.income.setEarnings(0);
        }

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
        final Integer earnings = income.getEarnings();
        this.income.setDeposit(earnings == null ? 0 : earnings - this.income.getWages());
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
        return this.currentPage.ordinal() == (this.canSelectZone ? 0 : 1);
    }

    public boolean isLastPage() {
        return this.currentPage.ordinal() == PageType.values().length - (this.type.isProfitable() ? 2 : 3);
    }
}
