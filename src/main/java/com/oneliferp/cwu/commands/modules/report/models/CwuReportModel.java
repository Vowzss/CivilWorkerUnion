package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.IdentityModel;

public class CwuReportModel extends ReportModel {
    @JsonProperty("identity")
    private IdentityModel identity;


    private CwuReportModel() {
        super(CwuBranch.CWU);
    }

    public CwuReportModel(final ProfileModel profile) {
        super(profile);
    }

    @Override
    public void setIdentity(final IdentityModel identity) {
        this.identity = identity;
    }

    @Override
    public IdentityModel getIdentity() {
        return this.identity;
    }

    /* Pageable implementation */
}
