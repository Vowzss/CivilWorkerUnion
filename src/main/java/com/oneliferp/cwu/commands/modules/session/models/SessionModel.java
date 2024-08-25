package com.oneliferp.cwu.commands.modules.session.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.session.misc.ParticipantType;
import com.oneliferp.cwu.commands.modules.session.misc.SessionType;
import com.oneliferp.cwu.commands.modules.session.misc.ZoneType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionPageType;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.misc.IdFactory;
import com.oneliferp.cwu.misc.pagination.Pageable;
import com.oneliferp.cwu.misc.pagination.PaginationContext;
import com.oneliferp.cwu.misc.pagination.PaginationRegistry;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.models.IncomeModel;
import com.oneliferp.cwu.models.PeriodModel;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionModel extends Pageable<SessionPageType> {
    @JsonProperty("id")
    private String id;

    @JsonProperty("employee")
    private IdentityModel employee;

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

    private SessionModel() {}

    public SessionModel(final IdentityModel identity) {
        this.id = IdFactory.get().generateID();

        this.period = new PeriodModel();
        this.period.start();

        this.employee = identity;
        this.type = SessionType.UNKNOWN;
        this.zone = ZoneType.UNKNOWN;
    }

    /* Getters & Setters */
    public String getId() {
        return this.id;
    }

    public IdentityModel getEmployee() {
        return this.employee;
    }

    public void setType(final SessionType type) {
        this.type = type;

        final var zone = this.type.getZone();
        if (zone == ZoneType.UNKNOWN) return;
        this.setZone(zone);
    }

    public SessionType getType() {
        return this.type;
    }

    public void setZone(final ZoneType zone) {
        this.zone = zone;
    }

    public ZoneType getZone() {
        return this.zone;
    }

    public void setInfo(@Nullable final String info) {
        this.info = (info == null || info.isBlank()) ? null : info;
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

    /* Methods */
    public String getEmployeeCid() {
        return this.employee.cid;
    }

    public HashSet<IdentityModel> getParticipants(final ParticipantType type) {
        return this.participants.get(type);
    }

    public boolean hasParticipants() {
        boolean isValid = false;
        for (final ParticipantType type : ParticipantType.values()) {
            if (this.participants.get(type).isEmpty()) continue;
            isValid = true;
            break;
        }
        return isValid;
    }

    public void addParticipants(final ParticipantType type, final List<IdentityModel> identities) {
        final var entries = this.participants.computeIfAbsent(type, k -> new HashSet<>());
        entries.addAll(identities);
    }

    public void clearParticipants(final ParticipantType type) {
        this.participants.get(type).clear();
    }

    public void setEarnings(final Integer earnings) {
        this.income.setEarnings(earnings);
    }

    /* Utils */
    public ProfileModel resolveEmployee() {
        return ProfileDatabase.get().getFromCid(this.employee.cid);
    }

    public boolean isWithinWeek() {
        return this.period.getStartedAt().isWithinWeek();
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

    /* Helpers */
    public String getDescriptionFormat(final boolean withIdentity) {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s Session : %s **(ID: %s)**", this.type.getEmoji(), this.type.getLabel(), this.id)).append("\n");
        sb.append(String.format("Emplacement : %s", this.zone.getLabel())).append("\n");
        if (withIdentity) sb.append(String.format("Employé : %s ", this.employee)).append("\n");
        sb.append(this.period.getEndedAt());
        return sb.toString();
    }

    public String getDescriptionFormat() {
        return this.getDescriptionFormat(true);
    }

    /* Pageable implementation */
    @Override
    public void start() {
        this.period.start();
        this.income = new IncomeModel();
        this.participants = new HashMap<>();

        this.pagination = new PaginationContext<>(PaginationRegistry.getSessionPages(this.type), SessionPageType.PREVIEW);

        // Prefill participants with available types
        for (final ParticipantType type : ParticipantType.values()) {
            this.participants.put(type, new HashSet<>());
        }
    }

    public void end() {
        this.period.end();

        if (this.income.getEarnings() == null) {
            this.income.setEarnings(0);
        }

        this.computeWages();
        this.computeDeposit();
    }

    @Override
    public void reset() {
        for (final ParticipantType type : ParticipantType.values()) {
            this.clearParticipants(type);
        }

        this.type = SessionType.UNKNOWN;
        this.zone = ZoneType.UNKNOWN;
        this.info = null;
        this.period = new PeriodModel();
        this.period.start();
    }

    @Override
    public boolean verify() {
        return this.employee != null && this.type != SessionType.UNKNOWN && this.zone != ZoneType.UNKNOWN && this.hasParticipants();
    }
}
