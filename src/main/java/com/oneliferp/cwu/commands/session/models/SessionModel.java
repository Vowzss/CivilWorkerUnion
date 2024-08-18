package com.oneliferp.cwu.commands.session.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.database.EmployeeDatabase;
import com.oneliferp.cwu.misc.IdFactory;
import com.oneliferp.cwu.misc.pagination.PaginationContext;
import com.oneliferp.cwu.misc.pagination.PaginationRegistry;
import com.oneliferp.cwu.models.EmployeeModel;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.models.IncomeModel;
import com.oneliferp.cwu.models.PeriodModel;
import com.oneliferp.cwu.commands.session.misc.ids.SessionPageType;
import com.oneliferp.cwu.commands.session.misc.ParticipantType;
import com.oneliferp.cwu.commands.session.misc.SessionType;
import com.oneliferp.cwu.commands.session.misc.ZoneType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionModel {
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

    @JsonIgnore
    private PaginationContext<SessionPageType> pagination;

    public SessionModel() {
    }

    public SessionModel(final EmployeeModel employee) {
        this.id = IdFactory.get().generateID();

        this.period = new PeriodModel();
        this.period.start();

        this.employee = employee.getIdentity();
        this.type = SessionType.UNKNOWN;
        this.zone = ZoneType.UNKNOWN;
    }

    /* Getters & Setters */

    public String getId() {
        return this.id;
    }

    public HashSet<IdentityModel> getParticipants(final ParticipantType type) {
        return this.participants.get(type);
    }

    public int getParticipantCount() {
        return this.participants.values().stream()
                .mapToInt(HashSet::size)
                .sum();
    }

    public void setType(final SessionType type) {
        this.type = type;

        // set predefined zone if exist
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

    public void begin() {
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

    public boolean isValid() {
        return this.employee != null && this.type != SessionType.UNKNOWN && this.zone != ZoneType.UNKNOWN && this.hasParticipants();
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

    /* Page Metadata */
    public SessionPageType getCurrentPage() {
        return this.pagination.getCurrent();
    }

    public void goNextPage() {
        this.pagination.setNext();
    }
    public void goPrevPage() {
        this.pagination.setPrev();
    }
    public void goPreviewPage() {
        this.pagination.setPreview();
    }
    public void goFirstPage() {
        this.pagination.setFirst();
    }

    public boolean isFirstPage() {
        return this.pagination.isFirst();
    }
    public boolean isLastPage() {
        return this.pagination.isLast();
    }

    public int getCurrentStep() {
        return this.pagination.getCurrentStep();
    }
    public int getMaxSteps() {
        return this.pagination.getMaxSteps();
    }

    public boolean hasPage(final SessionPageType type) {
        return this.pagination.contains(type);
    }

    /* Utils */
    public String getEmployeeCid() {
        return this.employee.cid;
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

    public EmployeeModel resolveEmployee() {
        return EmployeeDatabase.get().getFromCid(this.employee.cid);
    }

    public boolean isWithinWeek() {
        return this.period.getStartedAt().isWithinWeek();
    }
}
