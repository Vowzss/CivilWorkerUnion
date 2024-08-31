package com.oneliferp.cwu.commands.modules.profile.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.misc.ProfilePageType;
import com.oneliferp.cwu.commands.modules.report.models.ReportModel;
import com.oneliferp.cwu.commands.modules.session.models.SessionModel;
import com.oneliferp.cwu.database.ReportDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import com.oneliferp.cwu.misc.pagination.Pageable;
import com.oneliferp.cwu.misc.pagination.PaginationContext;
import com.oneliferp.cwu.misc.pagination.PaginationRegistry;
import com.oneliferp.cwu.models.CwuIdentityModel;
import com.oneliferp.cwu.utils.SimpleDate;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ProfileModel extends Pageable<ProfilePageType> {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("identity")
    private CwuIdentityModel identity;

    @JsonProperty("joinedAt")
    private SimpleDate joinedAt;

    public ProfileModel() {
    }

    public Long getId() {
        return this.id;
    }

    /* Getters & Setters */
    public void setId(final Long id) {
        this.id = id;
    }

    public CwuIdentityModel getIdentity() {
        return this.identity;
    }

    public void setIdentity(final CwuIdentityModel identity) {
        this.identity = identity;
    }

    public SimpleDate getJoinedAt() {
        return this.joinedAt;
    }

    public void setJoinedAt(final SimpleDate joinedAt) {
        this.joinedAt = joinedAt;
    }

    /* Methods */
    public String getCid() {
        if (this.identity == null) return null;
        return this.identity.getCid();
    }

    public CwuRank getRank() {
        if (this.identity == null) return null;
        return this.identity.getRank();
    }

    public void setRank(final CwuRank rank) {
        this.identity.setRank(rank);
    }

    public CwuBranch getBranch() {
        if (this.identity == null) return null;
        return this.identity.getBranch();
    }

    public void setBranch(final CwuBranch branch) {
        this.identity.setBranch(branch);
    }

    public boolean isAdministration() {
        return this.getBranch() == CwuBranch.CWU;
    }

    /* Utils */
    public SessionModel resolveLatestSession() {
        return SessionDatabase.get().resolveByEmployee(this.getCid()).stream()
                .filter(session -> session.getPeriod().getStartedAt() != null)
                .max(Comparator.comparing(session -> session.getPeriod().getStartedAt()))
                .orElse(null);
    }

    public List<SessionModel> resolveWeekSessions() {
        return SessionDatabase.get().resolveByEmployee(this.getCid())
                .stream().filter(SessionModel::isWithinWeek)
                .toList();
    }

    public List<SessionModel> resolveSessions() {
        return SessionDatabase.get().resolveByEmployee(this.getCid());
    }

    public ReportModel resolveLatestReport() {
        return ReportDatabase.get().resolveByEmployee(this.getCid()).stream()
                .max(Comparator.comparing(ReportModel::getCreatedAt))
                .orElse(null);
    }

    public List<ReportModel> resolveWeekReports() {
        return ReportDatabase.get().resolveByEmployee(this.getCid())
                .stream().filter(ReportModel::isWithinWeek)
                .toList();
    }

    public List<ReportModel> resolveReports() {
        return ReportDatabase.get().resolveByEmployee(this.getCid());
    }

    public Integer resolveSalaryTokens(final Collection<SessionModel> sessions, final Collection<ReportModel> reports) {
        final CwuRank rank = this.identity.getRank();

        return (int) Math.floor(SessionDatabase.resolveEarnings(sessions) * rank.getSessionRoyalty()) +
               (int) Math.floor(ReportDatabase.resolveEarnings(reports) * rank.getBranchRoyalty());
    }

    public Integer resolveSalaryPoints(final Collection<SessionModel> sessions, final Collection<ReportModel> reports) {
        return SessionDatabase.resolvePoints(sessions) + ReportDatabase.resolvePoints(reports);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.identity.getRank().getLabel(), this.identity);
    }

    public String getWeekStats() {
        final var sessions = this.resolveWeekSessions();
        final var reports = this.resolveWeekReports();

        return String.format("""
                        %s
                        Sessions: %d | Rapports: %s""",
                this, sessions.size(), reports.size()
        );
    }

    public String getOverallStats() {
        final var sessions = this.resolveSessions();
        final var reports = this.resolveReports();

        return String.format("""
                        %s
                        Sessions: %d | Rapports: %s""",
                this, sessions.size(), reports.size()
        );
    }

    /* Helpers */
    public String getDescriptionFormat() {

        String sb = "**Informations général :**" + "\n" +
                    String.format("Identité : %s", this.identity) + "\n" +
                    String.format("Identifiant discord : <@%d>", this.id) + "\n" +
                    String.format("Recruté le: %s", this.joinedAt.getDate()) + "\n\n" +
                    "**Informations CWU :**" + "\n" +
                    String.format("Division : %s (%s)\n", this.getBranch(), this.getBranch().getMeaning()) +
                    String.format("Grade : %s\n", this.identity.getRank().getLabel());
        return sb;
    }

    public String getStatsFormat() {
        final StringBuilder sb = new StringBuilder();

        sb.append("**Statistiques de la semaine :**").append("\n");
        sb.append(String.format("Nombre de session(s) : %d", this.resolveWeekSessions().size())).append("\n");
        sb.append(String.format("Nombre de rapport(s) : %d", this.resolveWeekReports().size())).append("\n\n");

        sb.append("**Statistiques globale :**").append("\n");
        sb.append(String.format("Nombre de session(s) : %d", this.resolveSessions().size())).append("\n");
        sb.append(String.format("Nombre de rapport(s) : %d", this.resolveReports().size())).append("\n\n");

        {
            final SessionModel s = this.resolveLatestSession();
            sb.append("**Dernière session effectuée :**").append("\n");
            sb.append(s == null ? "`Information inexistante`" : s.getDescriptionFormat(false)).append("\n\n");
        }

        {
            final ReportModel r = this.resolveLatestReport();
            sb.append("**Dernier rapport soumis :**").append("\n");
            sb.append(r == null ? "`Information inexistante`" : r.getDescriptionFormat(false)).append("\n");
        }

        return sb.toString();
    }

    public String getSalaryFormat() {
        final StringBuilder sb = new StringBuilder();

        sb.append("**Salaire de la semaine :**").append("\n");

        {
            final var sessions = this.resolveWeekSessions();
            final var reports = this.resolveWeekReports();

            sb.append(String.format("Tokens : %d", this.resolveSalaryTokens(sessions, reports))).append("\n");
            sb.append(String.format("Points de loyauté : %d", this.resolveSalaryPoints(sessions, reports)));
        }

        return sb.toString();
    }

    /* Pageable implementation */
    @Override
    public void start() {
        this.pagination = new PaginationContext<>(PaginationRegistry.getProfilePages(), ProfilePageType.PREVIEW);
        this.joinedAt = SimpleDate.now();
    }

    @Override
    public void end() {

    }

    @Override
    public void reset() {

    }

    @Override
    public boolean verify() {
        return false;
    }
}

