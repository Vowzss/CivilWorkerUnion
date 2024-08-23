package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.IdentityModel;

public class CwuReportModel extends ReportModel {
    private CwuReportModel() {
        super();

        this.branch = CwuBranch.CWU;
    }

    public CwuReportModel(final ProfileModel employee) {
        super(employee);
    }

    @JsonProperty("identity")
    private IdentityModel identity;

    @Override
    public void setIdentity(IdentityModel identity) {
        this.identity = identity;
    }

    @Override
    public IdentityModel getIdentity() {
        return this.identity;
    }
}
