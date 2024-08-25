package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.IdentityModel;

public class DtlReportModel extends ReportModel {
    @JsonProperty("tenant")
    private IdentityModel tenant;

    @JsonProperty("healthiness")
    private String healthiness;

    private DtlReportModel() {
        super(CwuBranch.DTL);
    }

    public DtlReportModel(final ProfileModel profile) {
        super(profile);
    }

    /* Getter & Setter */
    @Override
    public void setIdentity(final IdentityModel identity) {
        this.tenant = identity;
    }

    @Override
    public IdentityModel getIdentity() {
        return this.tenant;
    }

    @Override
    public void setHealthiness(final String healthiness) {
        this.healthiness = healthiness;
    }

    @Override
    public String getHealthiness() {
        return this.healthiness;
    }

    /* Pageable implementation */
    @Override
    public void reset() {
        super.reset();

        this.tenant = null;
        this.healthiness = null;
    }
}
