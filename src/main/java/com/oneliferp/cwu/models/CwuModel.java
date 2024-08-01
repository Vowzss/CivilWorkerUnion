package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.database.SessionDatabase;
import com.oneliferp.cwu.utils.SimpleDate;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;

import java.util.Comparator;

public class CwuModel {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("identity")
    private IdentityModel identity;

    @JsonProperty("branch")
    private CwuBranch branch;

    @JsonProperty("rank")
    private CwuRank rank;

    @JsonProperty("joinedAt")
    private SimpleDate joinedAt;

    public CwuModel() {}

    public CwuModel(final long id, final IdentityModel identity, final CwuBranch branch, final CwuRank rank) {
        this.id = id;
        this.identity = identity;
        this.branch = branch;
        this.rank = rank;
        this.joinedAt = SimpleDate.now();
    }

    /*
    Getters
    */
    public Long getId() {
        return this.id;
    }

    public String getCid() {
        return this.identity.cid;
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

    /*
    Utils
    */
    public SessionModel getLatestSession() {
        return SessionDatabase.get().getSessionsByCwu(this).stream()
                .filter(session -> session.getPeriod().getStartedAt() != null)
                .max(Comparator.comparing(session -> session.getPeriod().getStartedAt()))
                .orElse(null);
    }

    public int getSessionCount() {
        return SessionDatabase.get().getSessionsByCwu(this).size();
    }

    public int getMonthlySessionCount() {
        return SessionDatabase.get().getSessionsByCwu(this)
                .stream().filter(SessionModel::isWithinMonth)
                .toList().size();
    }

    public int getWeeklySessionCount() {
        return SessionDatabase.get().getSessionsByCwu(this)
                .stream().filter(SessionModel::isWithinWeek)
                .toList().size();
    }
}

