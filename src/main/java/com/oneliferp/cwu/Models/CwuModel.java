package com.oneliferp.cwu.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;

import java.util.ArrayList;
import java.util.List;

public class CwuModel {
    @Expose
    @SerializedName("id")
    private Long userID;

    @Expose
    @SerializedName("firstname")
    private String firstName;

    @Expose
    @SerializedName("lastname")
    private String lastName;

    @Expose
    @SerializedName("cid")
    private String cid;

    @Expose
    @SerializedName("branch")
    private CwuBranch branch;

    @Expose
    @SerializedName("rank")
    private CwuRank rank;

    public List<SessionModel> sessions;

    public CwuModel(final Long userID, final String firstName, final String lastName, final String cid, final CwuBranch branch, final CwuRank rank) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cid = cid.contains("#") ? cid.substring(0, cid.indexOf("#")) : cid;
        this.branch = branch;
        this.rank = rank;

        this.sessions = new ArrayList<>();
    }

    public CwuModel() {
        this.sessions = new ArrayList<>();
    }

    /*
    Getters
    */
    @JsonIgnore
    public Long getUserID() {
        return this.userID;
    }

    @JsonIgnore
    public String getFirstName() {
        return this.firstName;
    }

    @JsonIgnore
    public String getLastName() {
        return this.lastName;
    }

    @JsonIgnore
    public String getCid() {
        return this.cid;
    }

    @JsonIgnore
    public String getIdentity() {
        return String.format("%s %s #%s", this.firstName, this.lastName, this.cid);
    }

    @JsonIgnore
    public CwuBranch getBranch() {
        return this.branch;
    }

    @JsonIgnore
    public CwuRank getRank() {
        return this.rank;
    }

    @JsonIgnore
    public int getSessionCount() {
        return this.sessions.size();
    }

    @JsonIgnore
    public int getWeeklySessionCount() {
        return this.sessions.stream().filter(SessionModel::isWithinWeek).toList().size();
    }

    @JsonIgnore
    public int getMonthlySessionCount() {
        return this.sessions.stream().filter(SessionModel::isWithinMonth).toList().size();
    }

    /*
    Setters
    */
    public void addSession(final SessionModel session) {
        this.sessions.add(session);
    }
}

