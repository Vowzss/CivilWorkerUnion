package com.oneliferp.cwu.Models;

import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.misc.CwuRank;

public class CwuModel {
    private final Long userID;

    private final String firstName;
    private final String lastName;
    private final String cid;

    private final CwuBranch branch;
    private final CwuRank rank;

    private int totalReportCount = 0;
    private int weekReportCount = 0;
    private final int monthReportCount = 0;

    public CwuModel(final Long userID, final String firstName, final String lastName, final String cid, final CwuBranch branch, final CwuRank rank) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cid = cid.contains("#") ? cid.substring(0, cid.indexOf("#")) : cid;
        this.branch = branch;
        this.rank = rank;
    }

    /*
    Getters
    */
    public Long getUserID() {
        return this.userID;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getCid() {
        return this.cid;
    }

    public String getIdentity() {
        return String.format("%s %s #%s", this.firstName, this.lastName, this.cid);
    }

    public CwuBranch getBranch() {
        return this.branch;
    }

    public CwuRank getRank() {
        return this.rank;
    }

    public int getTotalReportCount() {
        return this.totalReportCount;
    }

    public int getWeeklyReportCount() {
        return this.weekReportCount;
    }

    public int getMonthlyReportCount() {
        return this.monthReportCount;
    }

    /*
    Setters
    */
    public void addTotalReportCount(final int count) {
        this.totalReportCount += count;
    }

    public void addWeekReportCount(final int count) {
        this.weekReportCount += count;
    }
}

