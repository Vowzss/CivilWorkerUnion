package com.oneliferp.cwu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.misc.ReportType;
import com.oneliferp.cwu.modules.report.misc.ReportPageType;
import com.oneliferp.cwu.modules.session.misc.SessionPageType;
import org.jetbrains.annotations.Nullable;

public class ReportModel {
    @JsonProperty("manager")
    private IdentityModel manager;

    @JsonProperty("type")
    private ReportType type;

    @JsonProperty("information")
    private String info;

    @JsonIgnore
    private ReportPageType currentPage;

    public ReportModel(final CwuModel cwu) {
        this.manager = cwu.getIdentity();
    }

    /*
    Setters
    */
    public void setType(final ReportType type) {
        this.type = type;
    }

    public void setInfo(@Nullable final String info) {
        this.info = (info == null || info.isBlank()) ? null : info;
    }

    public void setCurrentPage(final ReportPageType page) {
        this.currentPage = page;
    }

    /*
    Getters
    */
    public String getManagerCid() {
        return this.manager.cid;
    }

    public ReportType getType() {
        return this.type;
    }

    public String getInfo() {
        return this.info;
    }

    public ReportPageType getCurrentPage() {
        return this.currentPage;
    }

    /*
    Utils
    */
    public int getCurrentStep() {
        return currentPage.ordinal();
    }

    public int getMaxSteps() {
        return SessionPageType.values().length - 1;
    }
}
