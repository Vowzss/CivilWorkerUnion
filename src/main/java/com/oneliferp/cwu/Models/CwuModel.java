package com.oneliferp.cwu.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneliferp.cwu.Database.SessionDatabase;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;

public class CwuModel {
    @Expose
    @SerializedName("id")
    private Long id;

    @Expose
    @SerializedName("identity")
    private IdentityModel identity;

    @Expose
    @SerializedName("branch")
    private CwuBranch branch;

    @Expose
    @SerializedName("rank")
    private CwuRank rank;

    public CwuModel(final IdentityModel identity, final CwuBranch branch, final CwuRank rank) {
        this.identity = identity;
        this.branch = branch;
        this.rank = rank;
    }

    /*
    Getters
    */
    public Long getId() { return this.id; }

    public String getCid() {
        return this.identity.cid;
    }

    public boolean isLinked() {
        return this.id != null;
    }

    public String getIdentity() {
        return identity.toString();
    }

    public CwuBranch getBranch() {
        return this.branch;
    }

    public CwuRank getRank() {
        return this.rank;
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

    /*
    Setters
    */
    public void setId(final Long id) {
        this.id = id;
    }
}

