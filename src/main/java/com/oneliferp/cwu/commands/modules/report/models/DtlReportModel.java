package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.CitizenIdentityModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtlReportModel extends ReportModel {
    @JsonProperty("tenant")
    protected CitizenIdentityModel tenant;

    @JsonProperty("rent")
    private Integer rent;

    @JsonProperty("healthiness")
    private String healthiness;

    DtlReportModel() {
        super(CwuBranch.DTL);
    }

    public DtlReportModel(final ProfileModel profile) {
        super(profile);
    }

    @Override
    public CitizenIdentityModel getIdentity() {
        return this.tenant;
    }

    /* Getter & Setter */
    @Override
    public void setIdentity(final CitizenIdentityModel identity) {
        this.tenant = identity;
    }

    @Override
    public Integer getRent() {
        return this.rent;
    }

    @Override
    public void setRent(final Integer rent) {
        this.rent = rent;
    }

    @Override
    public String getHealthiness() {
        return this.healthiness;
    }

    @Override
    public void setHealthiness(final String healthiness) {
        this.healthiness = healthiness;
    }

    /* Pageable implementation */
    @Override
    public void reset() {
        super.reset();

        this.tenant = null;
        this.healthiness = null;
    }

    @Override
    public boolean verify() {
        return switch (this.type) {
            default -> throw new IllegalStateException("Unexpected value: " + this.type);
            case HOUSING_ATTRIBUTION, HOUSING_PAYMENT -> super.verify() && this.tenant != null && this.rent != null;
            case HOUSING_EVICTION -> super.verify() && this.tenant != null;
            case HOUSING_CLEANING -> super.verify() && this.healthiness != null;
        };
    }
}
