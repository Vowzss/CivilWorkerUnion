package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.IdentityModel;

public class DrtReportModel extends ReportModel {
    @JsonProperty("tax")
    private Integer tax;

    @JsonProperty("tenant")
    private IdentityModel tenant;

    @JsonProperty("healthiness")
    private String healthiness;

    private DrtReportModel() {
        super();

        this.branch = CwuBranch.DRT;
    }

    public DrtReportModel(final ProfileModel employee) {
        super(employee);
    }

    /* Getter & Setter */
    @Override
    public void setIdentity(IdentityModel identity) {
        this.tenant = identity;
    }

    @Override
    public IdentityModel getIdentity() {
        return this.tenant;
    }

    @Override
    public void setTax(final Integer tax) {
        this.tax = tax;
    }

    @Override
    public Integer getTax() {
        return this.tax;
    }

    @Override
    public void setHealthiness(final String healthiness) {
        this.healthiness = healthiness;
    }

    @Override
    public String getHealthiness() {
        return this.healthiness;
    }

    /* Methods */
    @Override
    public void reset() {
        super.reset();

        this.tax = null;
        this.tenant = null;
        this.healthiness = null;
    }
}
