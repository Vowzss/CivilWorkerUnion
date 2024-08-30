package com.oneliferp.cwu.commands.modules.session.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.commands.modules.session.misc.CitizenType;
import com.oneliferp.cwu.commands.modules.session.misc.SessionType;
import com.oneliferp.cwu.commands.modules.session.misc.ZoneType;
import com.oneliferp.cwu.commands.modules.session.misc.actions.SessionPageType;
import com.oneliferp.cwu.database.ProfileDatabase;
import com.oneliferp.cwu.misc.IdFactory;
import com.oneliferp.cwu.misc.pagination.Pageable;
import com.oneliferp.cwu.misc.pagination.PaginationContext;
import com.oneliferp.cwu.misc.pagination.PaginationRegistry;
import com.oneliferp.cwu.models.CitizenIdentityModel;
import com.oneliferp.cwu.models.CwuIdentityModel;
import com.oneliferp.cwu.models.IncomeModel;
import com.oneliferp.cwu.models.PeriodModel;
import com.oneliferp.cwu.utils.Toolbox;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionModel extends Pageable<SessionPageType> {
    @JsonProperty("id")
    private String id;

    @JsonProperty("employee")
    private CwuIdentityModel employee;

    @JsonProperty("citizens")
    private HashSet<CitizenIdentityModel> citizens;

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

    private SessionModel() {
    }

    public SessionModel(final CwuIdentityModel identity) {
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

    public CwuIdentityModel getEmployee() {
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

    public HashSet<CitizenIdentityModel> getCitizens() {
        return this.citizens;
    }

    public PeriodModel getPeriod() {
        return this.period;
    }

    public IncomeModel getIncome() {
        return this.income;
    }

    /* Methods */
    public String getEmployeeCid() {
        return this.employee.getCid();
    }

    public boolean hasCitizens() {
        return this.citizens.isEmpty();
    }

    public void addCitizens(final List<CitizenIdentityModel> identities) {
        this.citizens.addAll(identities);
    }

    public List<CitizenIdentityModel> getCitizensWithType(final CitizenType type) {
        return this.citizens.stream().filter(c -> c.getType() == type).toList();
    }

    public void removeCitizensWithType(final CitizenType type) {
        this.citizens.removeIf(c -> c.getType() == type);
    }

    public void setEarnings(final Integer earnings) {
        this.income.setEarnings(earnings);
    }

    /* Utils */
    public ProfileModel resolveEmployee() {
        return ProfileDatabase.get().getFromCid(this.employee.getCid());
    }

    public boolean isWithinWeek() {
        return this.period.getStartedAt().isWithinWeek();
    }

    public void computeWages() {
        final int wages = this.citizens.stream()
                .mapToInt(entry -> entry.getType().getWage())
                .sum();

        this.income.setWages(wages);
    }

    public void computeDeposit() {
        final Integer earnings = income.getEarnings();
        this.income.setDeposit(earnings == null ? 0 : earnings - this.income.getWages());
    }

    public int getParticipantCount() {
        return this.citizens.size();
    }

    public int getAntiCitizenCount() {
        return this.citizens.stream()
                .filter(e -> e.getType() == CitizenType.ANTI_CITIZEN)
                .toList().size();
    }

    public int getCitizenCount() {
        return this.citizens.stream()
                .filter(e -> e.getType() != CitizenType.ANTI_CITIZEN)
                .toList().size();
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

    public String getDisplayFormat() {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s **Session :** %s", this.type.getEmoji(), this.type.getLabel())).append("\n");
        sb.append(String.format("**Emplacement :** %s", this.zone.getLabel())).append("\n");
        sb.append(String.format("**Employé :** %s", this.employee)).append("\n");
        sb.append(String.format("**Date :** %s", this.period.getEndedAt())).append("\n\n");

        sb.append(String.format("**Nombre de citoyens :** %d", this.getCitizenCount())).append("\n");
        sb.append(String.format("**Nombre d'anti-citoyens :** %d", this.getAntiCitizenCount())).append("\n\n");

        this.citizens.stream().collect(Collectors.groupingBy(CitizenIdentityModel::getType)).forEach((type, identities) -> {
            sb.append(String.format("**%s(s) présent(s) :**", type.getLabel())).append("\n");
            sb.append(identities.isEmpty() ? "Aucun" : Toolbox.flatten(identities, false)).append("\n");
        });

        sb.append("\n");
        sb.append("**Informations supplémentaires :**").append("\n").append(this.info == null ? "Rien à signaler" : info).append("\n\n");
        sb.append(String.format("**Gains de la session :** %d tokens", this.income.getEarnings())).append("\n");
        sb.append(String.format("**Paies distribués :** %s tokens", this.income.getWages()));
        return sb.toString();
    }

    /* Pageable implementation */
    @Override
    public void start() {
        this.period.start();
        this.income = new IncomeModel();
        this.citizens = new HashSet<>();

        this.pagination = new PaginationContext<>(PaginationRegistry.getSessionPages(this.type), SessionPageType.PREVIEW);
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
        this.citizens.clear();
        this.type = SessionType.UNKNOWN;
        this.zone = ZoneType.UNKNOWN;
        this.info = null;
        this.period = new PeriodModel();
        this.period.start();
    }

    @Override
    public boolean verify() {
        return this.employee != null && this.type != SessionType.UNKNOWN && this.zone != ZoneType.UNKNOWN && this.hasCitizens();
    }
}
