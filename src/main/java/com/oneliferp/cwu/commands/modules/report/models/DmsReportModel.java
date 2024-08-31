package com.oneliferp.cwu.commands.modules.report.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneliferp.cwu.commands.modules.profile.models.ProfileModel;
import com.oneliferp.cwu.misc.CwuBranch;
import com.oneliferp.cwu.models.CitizenIdentityModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DmsReportModel extends ReportModel {
    @JsonProperty("patient")
    protected CitizenIdentityModel patient;

    @JsonProperty("tax")
    private Integer tax;

    @JsonProperty("medical")
    private String medical;

    DmsReportModel() {
        super(CwuBranch.DMS);
    }

    public DmsReportModel(final ProfileModel profile) {
        super(profile);
    }

    @Override
    public CitizenIdentityModel getIdentity() {
        return this.patient;
    }

    /* Getter & Setter */
    @Override
    public void setIdentity(final CitizenIdentityModel identity) {
        this.patient = identity;
    }

    @Override
    public Integer getTax() {
        return this.tax;
    }

    @Override
    public void setTax(final Integer tax) {
        this.tax = tax;
    }

    @Override
    public String getMedical() {
        return this.medical;
    }

    @Override
    public void setMedical(final String medical) {
        this.medical = medical;
    }

    /* Pageable implementation */
    @Override
    public void reset() {
        super.reset();

        this.patient = null;
        this.tax = null;
        this.medical = null;
    }

    @Override
    public boolean verify() {
        return switch (this.type) {
            default -> throw new IllegalStateException("Unexpected value: " + this.type);
            case BUSINESS_ATTRIBUTION -> super.verify() && this.patient != null && this.medical != null;
            case MEDICAL_ORDER -> super.verify() && this.tax != null;
        };
    }
}
