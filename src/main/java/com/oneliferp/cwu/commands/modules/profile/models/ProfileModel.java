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
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.SimpleDate;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ProfileModel extends Pageable<ProfilePageType> {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("identity")
    private IdentityModel identity;

    @JsonProperty("branch")
    private CwuBranch branch;

    @JsonProperty("joinedAt")
    private SimpleDate joinedAt;

    public ProfileModel() {
    }

    /* Getters & Setters */
    public void setId(final Long id) {
        this.id = id;
    }
    public Long getId() {
        return this.id;
    }

    public void setIdentity(final IdentityModel identity) {
        this.identity = identity;
    }
    public IdentityModel getIdentity() {
        return this.identity;
    }

    public void setBranch(final CwuBranch branch) {
        this.branch = branch;
    }
    public CwuBranch getBranch() {
        return this.branch;
    }

    public void setJoinedAt(final SimpleDate joinedAt) {
        this.joinedAt = joinedAt;
    }
    public SimpleDate getJoinedAt() {
        return this.joinedAt;
    }

    /* Methods */
    public String getCid() {
        return this.identity.cid;
    }

    public void setRank(final CwuRank rank) {
        this.identity.rank = rank;
    }

    public CwuRank getRank() {
        return this.identity.rank;
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
        final CwuRank rank = this.identity.rank;

        return (int) Math.floor(SessionDatabase.resolveEarnings(sessions) * rank.getSessionRoyalty()) +
               (int) Math.floor(ReportDatabase.resolveEarnings(reports) * rank.getBranchRoyalty());
    }

    public Integer resolveSalaryPoints(final Collection<SessionModel> sessions, final Collection<ReportModel> reports) {
        return SessionDatabase.resolvePoints(sessions) + ReportDatabase.resolvePoints(reports);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.identity.rank.getLabel(), this.identity);
    }

    public String getWeekStats() {
        final var sessions = this.resolveWeekSessions();
        final var reports = this.resolveWeekReports();

        return String.format("""
                        %s
                        Sessions: %d | Rapports: %s
                        Salaire : %d token(s) & %d point(s)""",
                this, sessions.size(), reports.size(),
                this.resolveSalaryTokens(sessions, reports), this.resolveSalaryPoints(sessions, reports)
        );
    }

    public String getOverallStats() {
        final var sessions = this.resolveSessions();
        final var reports = this.resolveReports();

        return String.format("""
                        %s
                        Sessions: %d | Rapports: %s
                        Salaire : %d token(s) & %d point(s)""",
                this, sessions.size(), reports.size(),
                this.resolveSalaryTokens(sessions, reports), this.resolveSalaryPoints(sessions, reports)
        );
    }

    /* Helpers */
    public String getDescriptionFormat() {
        final StringBuilder sb = new StringBuilder();
        sb.append("**Informations général :**").append("\n");
        sb.append(String.format("Identité : %s", this.identity)).append("\n");
        sb.append(String.format("Identifiant discord : <@%d>", this.id)).append("\n");
        sb.append(String.format("Recruté le: %s", this.joinedAt.getDate())).append("\n\n");

        sb.append("**Informations CWU :**").append("\n");
        sb.append(String.format("Division : %s (%s)\n", this.branch, this.branch.getMeaning()));
        sb.append(String.format("Grade : %s\n", this.identity.rank.getLabel()));
        return sb.toString();
    }

    public String getStatsFormat() {
        final StringBuilder sb = new StringBuilder();
        {
            final SessionModel s = this.resolveLatestSession();
            sb.append("**Dernière session effectuée :**").append("\n");
            sb.append(s == null ? "`Information inexistante`" : s.getDescriptionFormat(false)).append("\n\n");
        }

        /*
        sb.append(String.format("**Nombre de sessions:** %d\n", cwu.getSessionCount()));
        sb.append(String.format("**Compteur hebdomadaire:** %d\n", cwu.getWeeklySessionCount()));
        */

        {
            final ReportModel r = this.resolveLatestReport();
            sb.append("**Dernier rapport soumis :**").append("\n");
            sb.append(r == null ? "`Information inexistante`" : r.getDescriptionFormat(false)).append("\n\n");
        }
        /*
        sb.append(String.format("**Nombre de rapports:** %d\n", cwu.getReportCount()));
        sb.append(String.format("**Compteur hebdomadaire:** %d\n", cwu.getWeeklyReportCount()));
        */

        return sb.toString();
    }

    /* Pageable implementation */
    @Override
    public void start() {
        this.pagination = new PaginationContext<>(PaginationRegistry.getProfilePages(), ProfilePageType.PREVIEW);
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

