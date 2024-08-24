package com.oneliferp.cwu.commands.modules.profile.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.report.models.ReportModel;
import com.oneliferp.cwu.commands.modules.session.models.SessionModel;
import com.oneliferp.cwu.database.ReportDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.SimpleDate;

import java.util.Comparator;
import java.util.List;

public class ProfileModel {
    @JsonProperty("id")
    private long id;

    @JsonProperty("identity")
    private IdentityModel identity;

    @JsonProperty("branch")
    private CwuBranch branch;

    @JsonProperty("joinedAt")
    private SimpleDate joinedAt;

    private ProfileModel() {
    }

    public ProfileModel(final long id, final IdentityModel identity, final CwuBranch branch, final SimpleDate joinedAt) {
        this.id = id;
        this.identity = identity;
        this.branch = branch;
        this.joinedAt = joinedAt;
    }

    /* Getters */
    public Long getId() {
        return this.id;
    }

    public IdentityModel getIdentity() {
        return this.identity;
    }

    public SimpleDate getJoinedAt() {
        return this.joinedAt;
    }

    public CwuBranch getBranch() {
        return this.branch;
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
                SessionDatabase.resolveEarnings(sessions) + ReportDatabase.resolveEarnings(reports), SessionDatabase.resolvePoints(sessions) + ReportDatabase.resolvePoints(reports)
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
                SessionDatabase.resolveEarnings(sessions) + ReportDatabase.resolveEarnings(reports), SessionDatabase.resolvePoints(sessions) + ReportDatabase.resolvePoints(reports)
        );
    }
}

