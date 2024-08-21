package com.oneliferp.cwu.commands.profile.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.database.ReportDatabase;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.commands.report.models.ReportModel;
import com.oneliferp.cwu.commands.session.models.SessionModel;
import com.oneliferp.cwu.models.IdentityModel;
import com.oneliferp.cwu.utils.SimpleDate;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;

import java.util.Comparator;

public class ProfileModel {
    @JsonProperty("id")
    private long id;

    @JsonProperty("identity")
    private IdentityModel identity;

    @JsonProperty("branch")
    private CwuBranch branch;

    @JsonProperty("rank")
    private CwuRank rank;

    @JsonProperty("joinedAt")
    private SimpleDate joinedAt;

    private ProfileModel() {}

    public ProfileModel(final long id, final IdentityModel identity, final CwuBranch branch, final CwuRank rank, final SimpleDate joinedAt) {
        this.id = id;
        this.identity = identity;
        this.branch = branch;
        this.rank = rank;
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

    public CwuRank getRank() {
        return this.rank;
    }

    /* Methods */
    public String getCid() {
        return this.identity.cid;
    }

    /* Utils */
    public SessionModel getLatestSession() {
        return SessionDatabase.get().getSessionsByCwu(this.getCid()).stream()
                .filter(session -> session.getPeriod().getStartedAt() != null)
                .max(Comparator.comparing(session -> session.getPeriod().getStartedAt()))
                .orElse(null);
    }

    public int getSessionCount() {
        return SessionDatabase.get().getSessionsByCwu(this.getCid()).size();
    }

    public int getWeeklySessionCount() {
        return SessionDatabase.get().getSessionsByCwu(this.getCid())
                .stream().filter(SessionModel::isWithinWeek)
                .toList().size();
    }

    public ReportModel getLatestReport() {
        return ReportDatabase.get().getReportsByCwu(this.getCid()).stream()
                .max(Comparator.comparing(ReportModel::getCreatedAt))
                .orElse(null);
    }

    public int getReportCount() {
        return ReportDatabase.get().getReportsByCwu(this.getCid()).size();
    }

    public int getWeeklyReportCount() {
        return ReportDatabase.get().getReportsByCwu(this.getCid())
                .stream().filter(ReportModel::isWithinWeek)
                .toList().size();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.rank.getLabel(), this.identity);
    }
}

